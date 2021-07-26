package thapl.com.fudis.domain.case

import thapl.com.fudis.data.Repo

class StopsUseCaseImpl(private val repo: Repo) : StopsUseCase {

    override fun getContext() = repo.getContext()

}