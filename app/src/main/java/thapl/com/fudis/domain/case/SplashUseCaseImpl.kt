package thapl.com.fudis.domain.case

import thapl.com.fudis.data.Repo

class SplashUseCaseImpl(private val repo: Repo) : SplashUseCase {

    override fun getContext() = repo.getContext()

}