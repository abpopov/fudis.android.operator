package mb.delivery.operator.notifications.socket

import com.neovisionaries.ws.client.OpeningHandshakeException
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFactory
import com.neovisionaries.ws.client.WebSocketFrame
import com.neovisionaries.ws.client.WebSocketState
import org.json.JSONObject
import java.io.IOException
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Level
import java.util.logging.Logger

class Socket(private var url: String) : Emitter() {

    private val logger = Logger.getLogger(Socket::class.java.name)
    private val counter = AtomicInteger(1)
    private val factory = WebSocketFactory().setConnectionTimeout(5000)
    private val channels = ConcurrentHashMap<String, Channel>()
    private val acks = HashMap<Long, Array<Any>>()
    private val headers = HashMap<String, String>()

    private var strategy: ReconnectStrategy? = null
    private var ws: WebSocket? = null
    private var listener: BasicListener? = null
    private var authToken: String? = null
    private var adapter: WebSocketAdapter? = null

    init {
        adapter = getAdapter()
        putDefaultHeaders()
    }

    private fun putDefaultHeaders() {
        headers["Accept-Encoding"] = "gzip, deflate, sdch"
        headers["Accept-Language"] = "en-US,en;q=0.8"
        headers["Pragma"] = "no-cache"
        headers["User-Agent"] =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36"
    }

    fun createChannel(name: String): Channel? {
        if (channels.containsKey(name)) {
            return channels[name]
        }
        val channel = Channel(name)
        channels[name] = channel
        return channel
    }

    fun getChannels() = channels

    fun getChannelByName(name: String?): Channel? {
        return channels[name]
    }

    fun setUrl(url: String) {
        this.url = url
    }

    fun setReconnection(strategy: ReconnectStrategy?) {
        this.strategy = strategy
    }

    fun setListener(listener: BasicListener?) {
        this.listener = listener
    }

    fun getLogger() = logger

    fun getHeaders() = headers

    fun getFactorySettings(): WebSocketFactory? {
        return factory
    }

    fun setAuthToken(token: String?) {
        authToken = token
    }

    private fun getAdapter(): WebSocketAdapter = object : WebSocketAdapter() {
        @Throws(Exception::class)
        override fun onConnected(websocket: WebSocket, headers: Map<String, List<String>>) {
            counter.set(1)
            strategy?.setAttemptsMade(0)
            val handshakeObject = JSONObject()
            handshakeObject.put("event", "#handshake")
            val `object` = JSONObject()
            `object`.put("authToken", authToken)
            handshakeObject.put("data", `object`)
            handshakeObject.put("cid", counter.getAndIncrement())
            websocket.sendText(handshakeObject.toString())
            listener?.onConnected(this@Socket, headers)
            super.onConnected(websocket, headers)
        }

        @Throws(Exception::class)
        override fun onDisconnected(
            websocket: WebSocket,
            serverCloseFrame: WebSocketFrame,
            clientCloseFrame: WebSocketFrame,
            closedByServer: Boolean
        ) {
            listener?.onDisconnected(
                this@Socket,
                serverCloseFrame,
                clientCloseFrame,
                closedByServer
            )
            reconnect()
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer)
        }

        @Throws(Exception::class)
        override fun onConnectError(websocket: WebSocket, exception: WebSocketException) {
            listener?.onConnectError(this@Socket, exception)
            reconnect()
            super.onConnectError(websocket, exception)
        }

        @Throws(Exception::class)
        override fun onTextMessage(websocket: WebSocket, text: String?) {
            if (text == null) {
                websocket.sendText("")
            }
            super.onTextMessage(websocket, text)
        }

        @Throws(Exception::class)
        override fun onFrame(websocket: WebSocket, frame: WebSocketFrame) {
            val `object` = JSONObject(frame.payloadText)
            logger.info("Message :$`object`")
            try {
                val dataObject = `object`.opt("data")
                val rid = `object`.opt("rid") as? Int ?: 0
                val cid = `object`.opt("cid") as? Int ?: 0
                val event = `object`.opt("event") as? String ?: ""
                when (Parser.parse(dataObject, event)) {
                    Parser.ParseResult.IS_AUTHENTICATED -> {
                        listener?.onAuthentication(
                            this@Socket,
                            (dataObject as JSONObject).getBoolean("isAuthenticated")
                        )
                        subscribeChannels()
                    }

                    Parser.ParseResult.PUBLISH -> handlePublish(
                        (dataObject as JSONObject).getString(
                            "channel"
                        ), dataObject.opt("data")
                    )

                    Parser.ParseResult.REMOVE_TOKEN -> setAuthToken(null)
                    Parser.ParseResult.SET_TOKEN -> {
                        val token = (dataObject as JSONObject).getString("token")
                        setAuthToken(token)
                        listener?.onSetAuthToken(token, this@Socket)
                    }

                    Parser.ParseResult.EVENT -> if (hasEventAck(event)) {
                        handleEmitAck(
                            event,
                            dataObject,
                            ack(cid.toLong())
                        )
                    } else {
                        handleEmit(event, dataObject)
                    }

                    Parser.ParseResult.ACK_RECEIVE -> if (acks.containsKey(rid.toLong())) {
                        val objects = acks.remove(rid.toLong())
                        if (objects != null) {
                            val fn = objects[1] as? Ack
                            if (fn != null) {
                                fn.call(
                                    (objects[0] as String),
                                    `object`.opt("error"),
                                    `object`.opt("data")
                                )
                            } else {
                                logger.warning("ack function is null with rid $rid")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                logger.severe(e.toString())
            }
            super.onFrame(websocket, frame)
        }

        @Throws(Exception::class)
        override fun onCloseFrame(websocket: WebSocket, frame: WebSocketFrame) {
            logger.warning("On close frame got called")
            super.onCloseFrame(websocket, frame)
        }

        @Throws(Exception::class)
        override fun onSendError(
            websocket: WebSocket,
            cause: WebSocketException,
            frame: WebSocketFrame
        ) {
            logger.severe("Error while sending data $cause")
            super.onSendError(websocket, cause, frame)
        }
    }

    fun emit(event: String, `object`: Any): Socket {
        EventThread.exec {
            val eventObject = JSONObject()
            try {
                eventObject.put("event", event)
                eventObject.put("data", `object`)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            ws?.sendText(eventObject.toString())
        }
        return this
    }

    fun emit(event: String, `object`: Any, ack: Ack): Socket {
        EventThread.exec {
            val eventObject = JSONObject()
            acks[counter.toLong()] = getAckObject(event, ack)
            try {
                eventObject.put("event", event)
                eventObject.put("data", `object`)
                eventObject.put("cid", counter.getAndIncrement())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            ws?.sendText(eventObject.toString())
        }
        return this
    }

    private fun subscribe(channel: String): Socket {
        EventThread.exec {
            val subscribeObject = JSONObject()
            try {
                subscribeObject.put("event", "#subscribe")
                val `object` = JSONObject()
                `object`.put("channel", channel)
                subscribeObject.put("data", `object`)
                subscribeObject.put("cid", counter.getAndIncrement())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            ws?.sendText(subscribeObject.toString())
        }
        return this
    }

    private fun getAckObject(event: String, ack: Ack): Array<Any> {
        return arrayOf(event, ack)
    }

    private fun subscribe(channel: String, ack: Ack): Socket {
        EventThread.exec {
            val subscribeObject = JSONObject()
            try {
                subscribeObject.put("event", "#subscribe")
                val `object` = JSONObject()
                acks[counter.toLong()] = getAckObject(channel, ack)
                `object`.put("channel", channel)
                subscribeObject.put("data", `object`)
                subscribeObject.put("cid", counter.getAndIncrement())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            ws?.sendText(subscribeObject.toString())
        }
        return this
    }

    private fun unsubscribe(channel: String): Socket {
        EventThread.exec {
            val subscribeObject = JSONObject()
            try {
                subscribeObject.put("event", "#unsubscribe")
                subscribeObject.put("data", channel)
                subscribeObject.put("cid", counter.getAndIncrement())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            ws?.sendText(subscribeObject.toString())
        }
        return this
    }

    private fun unsubscribe(channel: String, ack: Ack): Socket {
        EventThread.exec {
            val subscribeObject = JSONObject()
            try {
                subscribeObject.put("event", "#unsubscribe")
                subscribeObject.put("data", channel)
                acks[counter.toLong()] = getAckObject(channel, ack)
                subscribeObject.put("cid", counter.getAndIncrement())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            ws?.sendText(subscribeObject.toString())
        }
        return this
    }

    fun publish(channel: String, data: Any): Socket {
        EventThread.exec {
            val publishObject = JSONObject()
            try {
                publishObject.put("event", "#publish")
                val `object` = JSONObject()
                `object`.put("channel", channel)
                `object`.put("data", data)
                publishObject.put("data", `object`)
                publishObject.put("cid", counter.getAndIncrement())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            ws?.sendText(publishObject.toString())
        }
        return this
    }

    fun publish(channel: String, data: Any, ack: Ack): Socket {
        EventThread.exec {
            val publishObject = JSONObject()
            try {
                publishObject.put("event", "#publish")
                val `object` = JSONObject()
                acks[counter.toLong()] = getAckObject(channel, ack)
                `object`.put("channel", channel)
                `object`.put("data", data)
                publishObject.put("data", `object`)
                publishObject.put("cid", counter.getAndIncrement())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            ws?.sendText(publishObject.toString())
        }
        return this
    }

    private fun ack(cid: Long): Ack {
        return object : Ack {
            override fun call(name: String, error: Any?, data: Any?) {
                EventThread.exec {
                    val `object` = JSONObject()
                    try {
                        `object`.put("error", error)
                        `object`.put("data", data)
                        `object`.put("rid", cid)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    ws?.sendText(`object`.toString())
                }
            }
        }
    }

    private fun subscribeChannels() {
        for (c in channels) {
            c.value.subscribe()
        }
    }

    fun setExtraHeaders(extraHeaders: Map<String, String>, overrideDefaultHeaders: Boolean) {
        if (overrideDefaultHeaders) {
            headers.clear()
        }
        headers.putAll(extraHeaders)
    }

    fun connect() {
        try {
            ws = factory.createSocket(url)
        } catch (e: IOException) {
            logger.severe(e.toString())
        }
        ws?.addExtension("permessage-deflate; client_max_window_bits")
        for (h in headers) {
            ws?.addHeader(h.key, h.value)
        }
        ws?.addListener(adapter)
        try {
            ws?.connect()
        } catch (e: OpeningHandshakeException) {
            logger.severe(e.toString())
            val sl = e.statusLine
            logger.info("=== Status Line ===")
            logger.info("HTTP Version  = \n" + sl.httpVersion)
            logger.info("Status Code   = \n" + sl.statusCode)
            logger.info("Reason Phrase = \n" + sl.reasonPhrase)
            // HTTP headers.
            val headers = e.headers
            logger.info("=== HTTP Headers ===")
            for (h in headers) {
                val name = h.key
                val values = h.value
                if (values == null || values.size == 0) {
                    logger.info(name)
                    continue
                }
                for (value in values) {
                    logger.info(name + value + "\n")
                }
            }
        } catch (e: WebSocketException) {
            listener?.onConnectError(this@Socket, e)
            reconnect()
        }
    }

    fun connectAsync() {
        try {
            ws = factory.createSocket(url)
        } catch (e: IOException) {
            logger.severe(e.toString())
        }
        ws?.addExtension("permessage-deflate; client_max_window_bits")
        for (h in headers) {
            ws?.addHeader(h.key, h.value)
        }
        ws?.addListener(adapter)
        ws?.connectAsynchronously()
    }

    private fun reconnect() {
        if (strategy == null) {
            logger.warning("Unable to reconnect: reconnection is null")
            return
        }
        if (strategy?.areAttemptsComplete() == true) {
            strategy?.setAttemptsMade(0)
            logger.warning("Unable to reconnect: max reconnection attempts reached")
            return
        }
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (strategy == null) {
                    logger.warning("Unable to reconnect: reconnection is null")
                    return
                }
                strategy?.processValues()
                connect()
                timer.cancel()
                timer.purge()
            }
        }, strategy?.reconnectInterval?.toLong() ?: 2000)
    }

    fun disconnect() {
        ws?.disconnect()
        strategy = null
    }

    fun getCurrentState(): WebSocketState? {
        return ws?.state
    }

    fun isConnected() = ws?.state == WebSocketState.OPEN

    fun disableLogging() {
        logger.level = Level.OFF
    }

    inner class Channel(private var channelName: String) {

        fun subscribe() {
            this@Socket.subscribe(channelName)
        }

        fun subscribe(ack: Ack) {
            this@Socket.subscribe(channelName, ack)
        }

        fun onMessage(listener: Listener) {
            this@Socket.onSubscribe(channelName, listener)
        }

        fun publish(data: Any) {
            this@Socket.publish(channelName, data)
        }

        fun publish(data: Any, ack: Ack) {
            this@Socket.publish(channelName, data, ack)
        }

        fun unsubscribe() {
            this@Socket.unsubscribe(channelName)
            channels.remove(this@Channel.channelName)
        }

        fun unsubscribe(ack: Ack) {
            this@Socket.unsubscribe(channelName, ack)
            channels.remove(this@Channel.channelName)
        }
    }

    protected fun finalize() {
        ws?.disconnect("Client socket garbage collected, closing connection")
    }
}