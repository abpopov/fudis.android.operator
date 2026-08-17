package mb.delivery.operator.ui.dialogs

import mb.delivery.operator.domain.case.HelpUseCase
import mb.delivery.operator.ui.base.BaseViewModel

class HelpViewModel(private val useCase: HelpUseCase) : BaseViewModel() {

    fun logout() {
        useCase.logout()
    }
}