package thapl.com.fudis.ui.dialogs

import thapl.com.fudis.domain.case.HelpUseCase
import thapl.com.fudis.ui.base.BaseViewModel

class HelpViewModel(private val useCase: HelpUseCase) : BaseViewModel() {

    fun logout() {
        useCase.logout()
    }
}