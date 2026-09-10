package mb.delivery.operator.domain.case

interface RegisterUseCase : BaseUseCase {
    fun getProjectId(): Int
    fun setProjectId(value: Int?)
    fun setProjectCode(value: String)
    fun setCustomHost(value: String)
    fun clearHostConfig()
    fun getHostDisplayLabel(): String
    suspend fun login(login: String, pwd: String): Any
}
