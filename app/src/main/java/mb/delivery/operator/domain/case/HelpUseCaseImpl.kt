package mb.delivery.operator.domain.case

import mb.delivery.operator.data.Repo

class HelpUseCaseImpl(private val repo: Repo) : HelpUseCase {

    override fun getContext() = repo.getContext()

    override fun logout() {
        repo.setUserToken(null)
    }
}