package thapl.com.fudis.domain.case

interface RegisterUseCase : BaseUseCase {
    suspend fun login(login: String, pwd: String): Any
}