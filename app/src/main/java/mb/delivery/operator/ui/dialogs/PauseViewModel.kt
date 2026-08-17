package mb.delivery.operator.ui.dialogs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import mb.delivery.operator.domain.case.PauseUseCase
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.ui.base.BaseViewModel
import mb.delivery.operator.utils.toCausePause
import mb.delivery.operator.utils.toTimePause

class PauseViewModel(private val useCase: PauseUseCase) : BaseViewModel() {

    private val _pauseState = MutableLiveData(Pair<Int?, Int?>(null, null))

    val pauseState = _pauseState.distinctUntilChanged()
    val working = MutableLiveData<Boolean>()
    val pauseRequest = MutableLiveData<ResultEntity<Boolean>>()

    init {
        working.postValue(useCase.getOrganizationState())
    }

    fun selectState(value: Int, period: Boolean) {
        val current = _pauseState.value
        _pauseState.postValue(if (period) Pair(value, current?.second) else Pair(current?.first, value))
    }

    fun drop() {
        _pauseState.postValue(Pair(null, null))
    }

    fun pause() {
        doRequest(pauseRequest) {
            useCase.setPause(_pauseState.value?.first.toTimePause(), _pauseState.value?.second.toCausePause())
        }
    }

    fun start() {
        doRequest(pauseRequest) {
            useCase.dropPause()
        }
    }

}