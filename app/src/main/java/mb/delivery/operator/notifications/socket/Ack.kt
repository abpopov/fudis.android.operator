package mb.delivery.operator.notifications.socket

interface Ack {
    fun call(name: String, error: Any?, data: Any?)
}