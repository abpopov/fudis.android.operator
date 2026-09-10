package mb.delivery.operator.domain.case

import mb.delivery.operator.domain.model.SplashDestination

interface SplashUseCase : BaseUseCase {
    fun isLoggedIn(): Boolean
    fun hasHostConfig(): Boolean
    suspend fun restoreSession(): SplashDestination
}
