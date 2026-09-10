package mb.delivery.operator.ui.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import mb.delivery.operator.data.api.model.EditProductApi
import mb.delivery.operator.data.api.model.OrderCartItemRequestApi
import mb.delivery.operator.data.api.model.OrderCartModificatorRequestApi
import mb.delivery.operator.domain.case.OrdersUseCase
import mb.delivery.operator.domain.model.CartEntity
import mb.delivery.operator.domain.model.MenuEntity
import mb.delivery.operator.domain.model.ORDER_STATUS_IN_DELIVERY
import mb.delivery.operator.domain.model.OrderEntity
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.ReceiptEntity
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.notifications.OperatorWebSocket
import mb.delivery.operator.ui.base.BaseViewModel
import mb.delivery.operator.utils.SingleLiveEvent
import mb.delivery.operator.utils.addHeaders

class OrdersViewModel(
    private val useCase: OrdersUseCase,
    private val socket: OperatorWebSocket
) : BaseViewModel() {

    companion object {
        private const val REFRESH_ORDER_DELAY = 1000L * 60L * 1L
    }

    private val _menuPos = MutableLiveData(0)

    private var ticker: Job? = null

    val menuPos = _menuPos.distinctUntilChanged()
    val orders = MutableLiveData<ResultEntity<List<OrderEntity>>>()
    val currentOrder = SingleLiveEvent<OrderEntity?>()
    val receipt = MutableLiveData<ResultEntity<ReceiptEntity>>()
    val status = SingleLiveEvent<ResultEntity<OrderEntity>>()
    val itemStatus = SingleLiveEvent<ResultEntity<OrderEntity>>()
    val menu = SingleLiveEvent<ResultEntity<MenuEntity>>()
    val scrollUp = SingleLiveEvent<Boolean>()
    val showMenu = MutableLiveData(useCase.getShowMenu())
    val showOrders = MutableLiveData(useCase.getShowOrders())
    val showStopList = MutableLiveData(useCase.getShowStopList())
    val showHighload = MutableLiveData(useCase.getShowHighload())
    val allowStatusWithoutDishesReady = MutableLiveData(useCase.getAllowStatusWithoutDishesReady())
    val allowEditOrder = MutableLiveData(useCase.getAllowEditOrder())
    val enableManualPosExport = MutableLiveData(useCase.getEnableManualPosExport())
    val organizations = SingleLiveEvent<ResultEntity<List<OrganizationKitchenEntity>>>()
    val editProducts = SingleLiveEvent<ResultEntity<List<EditProductApi>>>()
    val exportPos = SingleLiveEvent<ResultEntity<OrderEntity>>()

    init {
        getOrders()
        socket.setOrderEventsListener {
            refresh()
        }
    }

    override fun onCleared() {
        socket.setOrderEventsListener(null)
        super.onCleared()
    }

    fun reInit() {
        if (ticker?.isActive == false) {
            getOrders()
        }
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
        ticker?.cancel()
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

    fun changeItemStatus(item: Int, nextStatus: Int) {
        doPostActionRequest(
            itemStatus,
            block = {
                useCase.changeItemStatus(item, nextStatus)
            }, action = { result ->
                if (result is ResultEntity.Success) {
                    applyUpdatedOrder(result.data)
                } else if (result is ResultEntity.Error) {
                    currentOrder.postValue(currentOrder.value)
                }
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
            }, action = { result ->
                if (result is ResultEntity.Success) {
                    applyUpdatedOrder(result.data)
                } else if (result is ResultEntity.Error) {
                    orders.postValue(orders.value)
                    currentOrder.postValue(currentOrder.value)
                }
            }
        )
    }

    fun updateOrder(id: Long, clientComment: String? = null, organizationId: Int? = null) {
        doPostActionRequest(
            status,
            block = {
                useCase.updateOrder(id, clientComment, organizationId)
            },
            action = { result ->
                if (result is ResultEntity.Success) {
                    applyUpdatedOrder(result.data)
                }
            }
        )
    }

    fun updateCart(id: Long, cartItems: List<OrderCartItemRequestApi>) {
        doPostActionRequest(
            status,
            block = {
                useCase.updateCart(id, cartItems)
            },
            action = { result ->
                if (result is ResultEntity.Success) {
                    applyUpdatedOrder(result.data)
                }
            }
        )
    }

    fun exportToPos(id: Long) {
        doPostActionRequest(
            exportPos,
            block = {
                useCase.exportToPos(id)
            },
            action = { result ->
                if (result is ResultEntity.Success) {
                    applyUpdatedOrder(result.data)
                }
            }
        )
    }

    fun loadOrganizations() {
        doRequest(organizations) {
            useCase.organizations()
        }
    }

    fun loadEditProducts(organizationId: Int) {
        doRequest(editProducts) {
            useCase.editProducts(organizationId)
        }
    }

    fun toCartRequest(items: List<CartEntity>): List<OrderCartItemRequestApi> {
        return items.filter { it.id >= 0 }.map { cart ->
            OrderCartItemRequestApi(
                catalogItemId = cart.item.id,
                count = cart.count,
                status = cart.status.takeIf { it >= 0 },
                modificators = cart.modifiers.map {
                    OrderCartModificatorRequestApi(
                        modificatorId = it.modificator.id,
                        count = it.count
                    )
                }
            )
        }
    }

    private fun applyUpdatedOrder(updated: OrderEntity) {
        val order = orders.value
        if (order is ResultEntity.Success) {
            val list = order.data.map {
                if (it.id == updated.id) updated else it
            }.addHeaders()
            orders.postValue(ResultEntity.Success(list))
            if (updated.status >= ORDER_STATUS_IN_DELIVERY && list.any {
                    it.status < ORDER_STATUS_IN_DELIVERY
                }) {
                scrollUp.postValue(true)
            }
        }
        val current = currentOrder.value
        if (current?.id == updated.id) {
            currentOrder.postValue(updated)
        }
    }

    fun getMenu() {
        doRequest(menu) {
            useCase.getMenu()
        }
    }

    fun updateMenu(data: MenuEntity) {
        showMenu.postValue(data.showMenu)
        showOrders.postValue(data.showOrders)
        showStopList.postValue(data.showStopList)
        showHighload.postValue(data.showHighload)
        allowStatusWithoutDishesReady.postValue(data.allowStatusWithoutDishesReady)
        allowEditOrder.postValue(data.allowEditOrder)
        enableManualPosExport.postValue(data.enableManualPosExport)
    }
}
