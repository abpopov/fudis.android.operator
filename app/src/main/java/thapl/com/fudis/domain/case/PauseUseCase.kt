package thapl.com.fudis.domain.case

interface PauseUseCase : BaseUseCase {
    fun getOrganizationState(): Boolean
    suspend fun setPause(time: Int?, cause: Int?): Boolean
    suspend fun dropPause(): Boolean
}