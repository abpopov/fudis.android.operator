package thapl.com.fudis.ui.splash

import thapl.com.fudis.domain.case.SplashUseCase
import thapl.com.fudis.ui.base.BaseViewModel

class SplashViewModel(private val useCase: SplashUseCase) : BaseViewModel() {

    fun isLoggedIn() = useCase.isLoggedIn()
}