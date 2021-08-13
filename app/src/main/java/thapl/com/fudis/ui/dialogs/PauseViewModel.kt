package thapl.com.fudis.ui.dialogs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import thapl.com.fudis.domain.case.PauseUseCase
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.base.BaseViewModel
import thapl.com.fudis.utils.toCausePause
import thapl.com.fudis.utils.toTimePause

class PauseViewModel(private val useCase: PauseUseCase) : BaseViewModel() {

    private val _pauseState = MutableLiveData(Pair<Int?, Int?>(null, null))

    val pauseState = Transformations.distinctUntilChanged(_pauseState)
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