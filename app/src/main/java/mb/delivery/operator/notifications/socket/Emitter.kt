package mb.delivery.operator.notifications.socket

import java.util.concurrent.ConcurrentHashMap

open class Emitter {

    private val singleCallbacks = ConcurrentHashMap<String, Listener>()
    private val singleAckCallbacks = ConcurrentHashMap<String, AckListener>()
    private val publishCallbacks = ConcurrentHashMap<String, Listener>()

    fun on(event: String, fn: Listener): Emitter {
        if (singleCallbacks.containsKey(event)) {
            singleCallbacks.remove(event)
        }
        singleCallbacks[event] = fn
        return this
    }

    fun onSubscribe(event: String, fn: Listener): Emitter {
        if (publishCallbacks.containsKey(event)) {
            publishCallbacks.remove(event)
        }
        publishCallbacks[event] = fn
        return this
    }

    fun on(event: String, fn: AckListener): Emitter {
        if (singleAckCallbacks.containsKey(event)) {
            singleAckCallbacks.remove(event)
        }
        singleAckCallbacks[event] = fn
        return this
    }


    fun handleEmit(event: String, data: Any?): Emitter {
        val listener = singleCallbacks[event]
        listener?.call(event, data)
        return this
    }

    fun handlePublish(event: String, data: Any?): Emitter {
        val listener = publishCallbacks[event]
        listener?.call(event, data)
        return this
    }

    fun hasEventAck(event: String): Boolean {
        return singleAckCallbacks[event] != null
    }

    fun handleEmitAck(event: String, data: Any?, ack: Ack): Emitter {
        val listener = singleAckCallbacks[event]
        listener?.call(event, data, ack)
        return this
    }


    interface Listener {
        fun call(name: String, data: Any?)
    }

    interface AckListener {
        fun call(name: String, data: Any?, ack: Ack)
    }

    fun removeEmitCallback(event: String) {
        singleCallbacks.remove(event)
        singleAckCallbacks.remove(event)
    }

    fun removeSubscribeCallback(event: String) {
        publishCallbacks.remove(event)
    }

    fun removeAllCallbacks() {
        for (key in singleCallbacks) {
            singleCallbacks.remove(key.key)
        }
        for (key in singleAckCallbacks) {
            singleAckCallbacks.remove(key.key)
        }
        for (key in publishCallbacks) {
            publishCallbacks.remove(key.key)
        }
    }

}