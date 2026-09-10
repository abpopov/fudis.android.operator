package mb.delivery.operator.domain.case

import mb.delivery.operator.data.Repo
import mb.delivery.operator.domain.model.SplashDestination
import mb.delivery.operator.notifications.OperatorWebSocket
import retrofit2.HttpException

class SplashUseCaseImpl(
    private val repo: Repo,
    private val socket: OperatorWebSocket
) : SplashUseCase {

    override fun getContext() = repo.getContext()

    override fun isLoggedIn() = repo.getRefreshToken().isNullOrEmpty().not()

    override fun hasHostConfig() = repo.hasHostConfig()

    override suspend fun restoreSession(): SplashDestination {
        if (!repo.hasHostConfig()) {
            return SplashDestination.PROJECT
        }
        if (repo.getRefreshToken().isNullOrEmpty()) {
            return SplashDestination.AUTH
        }
        return try {
            repo.refreshSession()
            SplashDestination.ORDERS
        } catch (ex: HttpException) {
            if (ex.code() == 401 || ex.code() == 400) {
                socket.clear()
                repo.clearSession()
                SplashDestination.AUTH
            } else {
                fallbackWhileOffline()
            }
        } catch (_: Exception) {
            fallbackWhileOffline()
        }
    }

    private fun fallbackWhileOffline(): SplashDestination {
        return if (repo.getUserToken().isNullOrEmpty()) {
            SplashDestination.AUTH
        } else {
            SplashDestination.ORDERS
        }
    }
}
