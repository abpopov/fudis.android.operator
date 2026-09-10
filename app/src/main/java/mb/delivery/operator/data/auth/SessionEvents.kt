package mb.delivery.operator.data.auth

import mb.delivery.operator.utils.SingleLiveEvent
import java.util.concurrent.atomic.AtomicBoolean

class SessionEvents {
    val expired = SingleLiveEvent<Unit>()
    private val notified = AtomicBoolean(false)

    fun notifyExpired() {
        if (notified.compareAndSet(false, true)) {
            expired.postValue(Unit)
        }
    }

    fun reset() {
        notified.set(false)
    }
}
