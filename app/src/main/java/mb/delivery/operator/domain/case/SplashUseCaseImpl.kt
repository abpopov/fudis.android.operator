package mb.delivery.operator.domain.case

import mb.delivery.operator.data.Repo

class SplashUseCaseImpl(private val repo: Repo) : SplashUseCase {

    override fun getContext() = repo.getContext()

    override fun isLoggedIn() = repo.getUserToken().isNullOrEmpty().not()

    override fun getProjectId() = repo.getProjectId()
}