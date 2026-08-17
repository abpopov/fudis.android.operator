package mb.delivery.operator.ui.splash

import mb.delivery.operator.domain.case.SplashUseCase
import mb.delivery.operator.ui.base.BaseViewModel

class SplashViewModel(private val useCase: SplashUseCase) : BaseViewModel() {

    fun isLoggedIn() = useCase.isLoggedIn()

    fun hasProject() = useCase.getProjectId() > 0
}