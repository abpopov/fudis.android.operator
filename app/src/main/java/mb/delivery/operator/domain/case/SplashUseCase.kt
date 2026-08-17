package mb.delivery.operator.domain.case

interface SplashUseCase : BaseUseCase {
    fun isLoggedIn(): Boolean
    fun getProjectId(): Int
}