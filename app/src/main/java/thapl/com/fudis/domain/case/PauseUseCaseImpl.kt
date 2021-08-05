package thapl.com.fudis.domain.case

import thapl.com.fudis.data.Repo

class PauseUseCaseImpl(private val repo: Repo) : PauseUseCase {

    override fun getContext() = repo.getContext()

}