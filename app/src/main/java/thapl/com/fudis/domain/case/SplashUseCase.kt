package thapl.com.fudis.domain.case

interface SplashUseCase : BaseUseCase {
    fun isLoggedIn(): Boolean
    fun getProjectId(): Int
}