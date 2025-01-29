package thapl.com.fudis.domain.case

interface RegisterUseCase : BaseUseCase {
    fun getProjectId(): Int
    fun setProjectId(value: Int?)
    suspend fun login(login: String, pwd: String): Any
}