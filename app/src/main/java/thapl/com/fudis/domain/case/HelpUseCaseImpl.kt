package thapl.com.fudis.domain.case

import thapl.com.fudis.data.Repo

class HelpUseCaseImpl(private val repo: Repo) : HelpUseCase {

    override fun getContext() = repo.getContext()

    override fun logout() {
        repo.setUserToken(null)
    }
}