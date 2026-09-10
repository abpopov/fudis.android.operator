package mb.delivery.operator.notifications

interface OperatorWebSocket {
    fun init(url: String, token: String, channel: String)
    fun start()
    fun stop()
    fun clear()
    fun setOrderEventsListener(listener: (() -> Unit)?)
}
