package thapl.com.fudis.ui.dialogs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import thapl.com.fudis.domain.case.PauseUseCase
import thapl.com.fudis.ui.base.BaseViewModel

class PauseViewModel(private val useCase: PauseUseCase) : BaseViewModel() {

    private val _pauseState = MutableLiveData(Pair<Int?, Int?>(null, null))

    val pauseState = Transformations.distinctUntilChanged(_pauseState)

    fun selectState(value: Int, period: Boolean) {
        val current = _pauseState.value
        _pauseState.postValue(if (period) Pair(value, current?.second) else Pair(current?.first, value))
    }

    fun drop() {
        _pauseState.postValue(Pair(null, null))
    }

}