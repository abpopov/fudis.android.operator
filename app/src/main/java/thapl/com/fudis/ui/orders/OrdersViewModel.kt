package thapl.com.fudis.ui.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import thapl.com.fudis.domain.case.OrdersUseCase
import thapl.com.fudis.domain.model.OrderEntity
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.base.BaseViewModel
import thapl.com.fudis.utils.SingleLiveEvent

class OrdersViewModel(private val useCase: OrdersUseCase) : BaseViewModel() {

    private val _menuPos = MutableLiveData(0)

    val menuPos = Transformations.distinctUntilChanged(_menuPos)
    val orders = MutableLiveData<ResultEntity<List<OrderEntity>>>()

    init {
        getOrders()
    }

    fun selectMenu(value: Int) {
        _menuPos.postValue(value)
    }

    fun refresh() {
        getOrders()
    }

    private fun getOrders() {
        doRequest(orders) {
            useCase.getOrders()
        }
    }
}