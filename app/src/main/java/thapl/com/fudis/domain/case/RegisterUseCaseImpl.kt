package thapl.com.fudis.domain.case

import thapl.com.fudis.data.Repo

class RegisterUseCaseImpl(private val repo: Repo) : RegisterUseCase {

    override fun getContext() = repo.getContext()

}