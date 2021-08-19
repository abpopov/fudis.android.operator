package thapl.com.fudis.ui.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import thapl.com.fudis.domain.case.OrdersUseCase
import thapl.com.fudis.domain.model.OrderEntity
import thapl.com.fudis.domain.model.ReceiptEntity
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.base.BaseViewModel
import thapl.com.fudis.utils.SingleLiveEvent

class OrdersViewModel(private val useCase: OrdersUseCase) : BaseViewModel() {

    companion object {
        private const val REFRESH_ORDER_DELAY = 1000L * 60L * 1L
    }

    private val _menuPos = MutableLiveData(0)

    private var ticker: Job? = null

    val menuPos = Transformations.distinctUntilChanged(_menuPos)
    val orders = MutableLiveData<ResultEntity<List<OrderEntity>>>()
    val order = MutableLiveData<ResultEntity<OrderEntity>>()
    val receipt = MutableLiveData<ResultEntity<ReceiptEntity>>()

    init {
        getOrders()
    }

    fun selectMenu(value: Int) {
        _menuPos.postValue(value)
    }

    fun refresh() {
        getOrders()
    }

    fun initOrder(order: OrderEntity) {

    }

    fun initReceipt(id: Long) {
        doRequest(receipt) {
            useCase.getReceipt(id)
        }
    }

    private fun getOrders() {
        ticker = doPostActionRequest(
            orders,
            block = {
                useCase.getOrders()
            },
            action = {
                delay(REFRESH_ORDER_DELAY)
                getOrders()
            }
        )

    }
}