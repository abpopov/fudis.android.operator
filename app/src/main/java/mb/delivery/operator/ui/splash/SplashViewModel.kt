package mb.delivery.operator.ui.splash

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mb.delivery.operator.domain.case.SplashUseCase
import mb.delivery.operator.domain.model.SplashDestination
import mb.delivery.operator.ui.base.BaseViewModel
import mb.delivery.operator.utils.SingleLiveEvent

class SplashViewModel(private val useCase: SplashUseCase) : BaseViewModel() {

    val destination = SingleLiveEvent<SplashDestination>()
    private var restoreJob: Job? = null

    fun start() {
        if (restoreJob?.isActive == true) {
            return
        }
        restoreJob = viewModelScope.launch {
            destination.postValue(useCase.restoreSession())
        }
    }
}
