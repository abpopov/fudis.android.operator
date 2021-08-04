package thapl.com.fudis.ui.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import thapl.com.fudis.domain.case.OrdersUseCase
import thapl.com.fudis.ui.base.BaseViewModel

class OrdersViewModel(private val useCase: OrdersUseCase) : BaseViewModel() {

    private val _menuPos = MutableLiveData(0)

    val menuPos = Transformations.distinctUntilChanged(_menuPos)

    fun selectMenu(value: Int) {
        _menuPos.postValue(value)
    }
}