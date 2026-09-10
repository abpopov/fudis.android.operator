package mb.delivery.operator.notifications.socket

import java.util.logging.Level
import java.util.logging.Logger

class ReconnectStrategy {

    companion object {
        private val LOGGER = Logger.getLogger(ReconnectStrategy::class.java.name)
    }

    var reconnectInterval = 0
    private var maxReconnectInterval = 0
    private var attemptsMade = 0
    private var reconnectDecay = 0f
    private var maxAttempts: Int? = null

    constructor() {
        LOGGER.level = Level.INFO
        reconnectInterval = 2000
        maxReconnectInterval = 30000
        reconnectDecay = 1f
        maxAttempts = null //forever
        attemptsMade = 0
    }

    fun setMaxAttempts(maxAttempts: Int?): ReconnectStrategy {
        this.maxAttempts = maxAttempts
        return this
    }

    fun setDelay(delay: Int): ReconnectStrategy {
        reconnectInterval = delay
        return this
    }

    fun setAttemptsMade(attemptsMade: Int) {
        this.attemptsMade = attemptsMade
    }

    constructor(
        reconnectInterval: Int,
        maxReconnectInterval: Int,
        reconnectDecay: Float,
        maxAttempts: Int
    ) {
        if (reconnectInterval > maxReconnectInterval) {
            this.reconnectInterval = maxReconnectInterval
        } else {
            this.reconnectInterval = reconnectInterval
        }
        this.maxReconnectInterval = maxReconnectInterval
        this.reconnectDecay = reconnectDecay
        this.maxAttempts = maxAttempts
        attemptsMade = 0
    }

    fun processValues() {
        attemptsMade++
        LOGGER.info("Attempt number :$attemptsMade")
        if (reconnectInterval < maxReconnectInterval) {
            reconnectInterval = (reconnectInterval * reconnectDecay).toInt()
            if (reconnectInterval > maxReconnectInterval) {
                reconnectInterval = maxReconnectInterval
            }
        }
    }

    fun areAttemptsComplete(): Boolean {
        return attemptsMade == maxAttempts
    }


}