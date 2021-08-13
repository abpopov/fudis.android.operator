package thapl.com.fudis.domain.case

import thapl.com.fudis.data.Repo

class PauseUseCaseImpl(private val repo: Repo) : PauseUseCase {

    override fun getContext() = repo.getContext()

    override fun getOrganizationState() = repo.getOrganizationState()

    override suspend fun setPause(time: Int?, cause: Int?): Boolean {
        val result = repo.stopOrganization(repo.getOrganizationId(), time, cause).success ?: false
        if (result) {
            repo.setOrganizationState(false)
        }
        return result.not()
    }

    override suspend fun dropPause(): Boolean {
        val result = repo.startOrganization(repo.getOrganizationId()).success ?: false
        if (result) {
            repo.setOrganizationState(true)
        }
        return result
    }

}