package mb.delivery.operator.notifications

import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFrame
import mb.delivery.operator.data.prefs.Prefs
import mb.delivery.operator.notifications.socket.BasicListener
import mb.delivery.operator.notifications.socket.Emitter
import mb.delivery.operator.notifications.socket.ReconnectStrategy
import mb.delivery.operator.notifications.socket.Socket
import org.json.JSONObject

class OperatorWebSocketImpl(private val prefs: Prefs) : OperatorWebSocket {

    private var url: String = ""
    private var token: String = ""
    private var channel: String = ""

    private var ws: Socket? = null
    private var orderEventsListener: (() -> Unit)? = null

    override fun init(url: String, token: String, channel: String) {
        val changed = this.url != url || this.token != token || this.channel != channel
        this.url = url
        this.token = token
        this.channel = channel
        if (changed && ws?.isConnected() == true) {
            stop()
        }
        start()
    }

    override fun start() {
        loadFromPrefsIfNeeded()
        if (url.isEmpty() || token.isEmpty() || channel.isEmpty()) {
            return
        }
        if (ws?.isConnected() == true) {
            return
        }
        if (ws == null) {
            ws = Socket(url)
        } else {
            ws?.setUrl(url)
        }
        ws?.setListener(object : BasicListener {
            override fun onConnected(socket: Socket, headers: Map<String, List<String>>) {
            }

            override fun onDisconnected(
                socket: Socket?,
                serverCloseFrame: WebSocketFrame?,
                clientCloseFrame: WebSocketFrame?,
                closedByServer: Boolean
            ) {
            }

            override fun onConnectError(socket: Socket?, exception: WebSocketException?) {
            }

            override fun onAuthentication(socket: Socket?, status: Boolean?) {
                if (status == true) {
                    attachChannelListener()
                }
            }

            override fun onSetAuthToken(token: String?, socket: Socket?) {
            }
        })
        val created = ws?.createChannel(channel)
        created?.subscribe()
        ws?.setAuthToken(token)
        ws?.setReconnection(ReconnectStrategy().setDelay(2000).setMaxAttempts(null))
        ws?.connectAsync()
    }

    override fun stop() {
        ws?.getChannels()?.forEach { (_, ch) ->
            ch.unsubscribe()
        }
        ws?.disconnect()
    }

    override fun clear() {
        stop()
        ws = null
        url = ""
        token = ""
        channel = ""
        orderEventsListener = null
    }

    override fun setOrderEventsListener(listener: (() -> Unit)?) {
        orderEventsListener = listener
        if (ws?.isConnected() == true) {
            attachChannelListener()
        }
    }

    private fun loadFromPrefsIfNeeded() {
        if (url.isNotEmpty() && token.isNotEmpty() && channel.isNotEmpty()) {
            return
        }
        url = prefs.getWsSocketUrl().orEmpty()
        token = prefs.getWsSocketToken().orEmpty()
        channel = prefs.getWsSocketChannel().orEmpty()
    }

    private fun attachChannelListener() {
        val name = channel
        if (name.isEmpty()) {
            return
        }
        ws?.getChannelByName(name)?.onMessage(object : Emitter.Listener {
            override fun call(eventName: String, data: Any?) {
                try {
                    val json = JSONObject(data?.toString() ?: return)
                    val type = json.optString("type")
                    if (type == TYPE_NEW_ORDER || type == TYPE_CHANGE_STATUS) {
                        orderEventsListener?.invoke()
                    }
                } catch (_: Exception) {
                }
            }
        })
    }

    companion object {
        private const val TYPE_NEW_ORDER = "new_order"
        private const val TYPE_CHANGE_STATUS = "change_status_order"
    }
}
