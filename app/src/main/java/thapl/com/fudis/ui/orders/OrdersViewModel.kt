package thapl.com.fudis.ui.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import thapl.com.fudis.domain.case.OrdersUseCase
import thapl.com.fudis.domain.model.ORDER_STATUS_IN_DELIVERY
import thapl.com.fudis.domain.model.OrderEntity
import thapl.com.fudis.domain.model.ReceiptEntity
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.base.BaseViewModel
import thapl.com.fudis.utils.SingleLiveEvent
import thapl.com.fudis.utils.addHeaders

class OrdersViewModel(private val useCase: OrdersUseCase) : BaseViewModel() {

    companion object {
        private const val REFRESH_ORDER_DELAY = 1000L * 60L * 1L
    }

    private val _menuPos = MutableLiveData(0)

    private var ticker: Job? = null

    val menuPos = Transformations.distinctUntilChanged(_menuPos)
    val orders = MutableLiveData<ResultEntity<List<OrderEntity>>>()
    val currentOrder = SingleLiveEvent<OrderEntity>()
    val receipt = MutableLiveData<ResultEntity<ReceiptEntity>>()
    val status = SingleLiveEvent<ResultEntity<Pair<Long, Int>>>()
    val scrollUp = SingleLiveEvent<Boolean>()

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
        currentOrder.postValue(order)
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

    fun changeStatus(id: Long, nextStatus: Int) {
        if (nextStatus == -1) {
            return
        }
        doPostActionRequest(
            status,
            block = {
                useCase.changeStatus(id, nextStatus)
            }, action = { pair ->
                if (pair is ResultEntity.Success) {
                    val order = orders.value
                    if (order is ResultEntity.Success) {
                        val list = order.data.onEach {
                            if (it.id == pair.data.first) {
                                it.status = pair.data.second
                            }
                        }.addHeaders()
                        orders.postValue(ResultEntity.Success(list))
                        if (pair.data.second >= ORDER_STATUS_IN_DELIVERY && list.any {
                                it.status < ORDER_STATUS_IN_DELIVERY
                            }) {
                            scrollUp.postValue(true)
                        }
                    }
                    val current = currentOrder.value
                    current?.let {
                        if (it.id == pair.data.first) {
                            it.status = pair.data.second
                            it.updatedAt = System.currentTimeMillis()
                            currentOrder.postValue(it)
                        }
                    }
                } else if (pair is ResultEntity.Error) {
                    orders.postValue(orders.value)
                    currentOrder.postValue(currentOrder.value)
                }
            }
        )
    }
}