package mb.delivery.operator.ui.orders

import android.os.SystemClock
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import mb.delivery.operator.data.api.model.OrderCartItemRequestApi
import mb.delivery.operator.data.api.model.OrderCartModificatorRequestApi
import mb.delivery.operator.domain.case.OrdersUseCase
import mb.delivery.operator.domain.model.CartEntity
import mb.delivery.operator.domain.model.EditProductsCatalog
import mb.delivery.operator.domain.model.MenuEntity
import mb.delivery.operator.domain.model.ORDER_STATUS_CANCELED
import mb.delivery.operator.domain.model.ORDER_STATUS_DELIVERED
import mb.delivery.operator.domain.model.ORDER_STATUS_IN_DELIVERY
import mb.delivery.operator.domain.model.OrderEntity
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.ReceiptEntity
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.notifications.OperatorWebSocket
import mb.delivery.operator.ui.base.BaseViewModel
import mb.delivery.operator.utils.SingleLiveEvent
import mb.delivery.operator.utils.addHeaders

data class PendingItemStatusChange(
    val itemId: Int,
    val nextStatus: Int,
    val actionLabelRes: Int,
    val endsAtElapsedMs: Long
)

class OrdersViewModel(
    private val useCase: OrdersUseCase,
    private val socket: OperatorWebSocket
) : BaseViewModel() {

    companion object {
        private const val REFRESH_ORDER_DELAY = 1000L * 60L * 1L
        /** Как в SPA ActiveOrderDetail: 10 с на отмену смены статуса позиции. */
        const val ITEM_STATUS_UNDO_MS = 10_000L
    }

    private val _menuPos = MutableLiveData(0)

    private var ticker: Job? = null
    private var pendingTickJob: Job? = null
    private val pendingItemJobs = mutableMapOf<Int, Job>()

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
    val cartItemReadyEnabled = MutableLiveData(useCase.getCartItemReadyEnabled())
    val allowEditOrder = MutableLiveData(useCase.getAllowEditOrder())
    val enableManualPosExport = MutableLiveData(useCase.getEnableManualPosExport())
    val showOrganization = MutableLiveData(useCase.getShowOrganization())
    val commentMode = MutableLiveData(useCase.getCommentMode())
    val organizations = SingleLiveEvent<ResultEntity<List<OrganizationKitchenEntity>>>()
    val editProducts = SingleLiveEvent<ResultEntity<EditProductsCatalog>>()
    val exportPos = SingleLiveEvent<ResultEntity<OrderEntity>>()
    /** Заказ закрыт/отменён — уйти с деталки. */
    val orderClosed = SingleLiveEvent<Long>()

    /** Отложенные смены статусов позиций (id → pending). */
    val pendingItemChanges = MutableLiveData<Map<Int, PendingItemStatusChange>>(emptyMap())
    /** Тик для обновления «через N с» в UI. */
    val pendingItemTick = MutableLiveData(0L)

    /** Каталог для «Добавить блюдо» (категории + items). */
    var editProductsCatalog: EditProductsCatalog? = null

    /** Черновик состава при редактировании на кастомной панели (с модификаторами). */
    val cartEditDraft = mutableListOf<OrderCartItemRequestApi>()
    val cartEditTitles = mutableListOf<String>()

    fun startCartEdit(items: List<OrderCartItemRequestApi>, titles: List<String>) {
        cartEditDraft.clear()
        cartEditDraft.addAll(items)
        cartEditTitles.clear()
        cartEditTitles.addAll(titles)
    }

    fun clearCartEdit() {
        cartEditDraft.clear()
        cartEditTitles.clear()
    }

    init {
        getOrders()
        socket.setOrderEventsListener {
            refresh()
        }
    }

    override fun onCleared() {
        socket.setOrderEventsListener(null)
        clearAllPendingItemStatuses()
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
        clearAllPendingItemStatuses()
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

    /**
     * Старт отложенной смены статуса позиции (10 с, как в SPA).
     * API вызывается только после таймера либо не вызывается при отмене.
     */
    /**
     * Как на бэке: не требовать READY по позициям, если фича готовности выкл
     * или явно разрешён bypass ([allowStatusWithoutDishesReady]).
     */
    fun skipDishReadyChecks(): Boolean {
        return cartItemReadyEnabled.value != true || allowStatusWithoutDishesReady.value == true
    }

    fun scheduleItemStatusChange(itemId: Int, nextStatus: Int, actionLabelRes: Int) {
        if (itemId < 0) return
        if (cartItemReadyEnabled.value != true) return
        cancelPendingItemStatus(itemId)
        val endsAt = SystemClock.elapsedRealtime() + ITEM_STATUS_UNDO_MS
        val pending = PendingItemStatusChange(
            itemId = itemId,
            nextStatus = nextStatus,
            actionLabelRes = actionLabelRes,
            endsAtElapsedMs = endsAt
        )
        pendingItemChanges.value = pendingItemChanges.value.orEmpty() + (itemId to pending)
        syncPendingTick()
        pendingItemJobs[itemId] = viewModelScope.launch {
            delay(ITEM_STATUS_UNDO_MS)
            commitPendingItemStatus(itemId)
        }
    }

    fun cancelPendingItemStatus(itemId: Int) {
        pendingItemJobs.remove(itemId)?.cancel()
        val map = pendingItemChanges.value.orEmpty().toMutableMap()
        if (map.remove(itemId) != null) {
            pendingItemChanges.value = map
        }
        syncPendingTick()
    }

    fun getPendingItemStatus(itemId: Int): PendingItemStatusChange? {
        return pendingItemChanges.value.orEmpty()[itemId]
    }

    fun pendingSecondsLeft(itemId: Int): Int {
        val pending = getPendingItemStatus(itemId) ?: return 0
        val left = pending.endsAtElapsedMs - SystemClock.elapsedRealtime()
        return ((left + 999) / 1000).toInt().coerceAtLeast(0)
    }

    private fun commitPendingItemStatus(itemId: Int) {
        val pending = pendingItemChanges.value.orEmpty()[itemId] ?: return
        pendingItemJobs.remove(itemId)
        val map = pendingItemChanges.value.orEmpty().toMutableMap()
        map.remove(itemId)
        pendingItemChanges.value = map
        syncPendingTick()
        changeItemStatus(itemId, pending.nextStatus)
    }

    private fun clearAllPendingItemStatuses() {
        pendingItemJobs.values.forEach { it.cancel() }
        pendingItemJobs.clear()
        if (pendingItemChanges.value.orEmpty().isNotEmpty()) {
            pendingItemChanges.value = emptyMap()
        }
        syncPendingTick()
    }

    private fun syncPendingTick() {
        val hasPending = pendingItemChanges.value.orEmpty().isNotEmpty()
        if (hasPending && pendingTickJob?.isActive != true) {
            pendingTickJob = viewModelScope.launch {
                while (isActive && pendingItemChanges.value.orEmpty().isNotEmpty()) {
                    pendingItemTick.value = SystemClock.elapsedRealtime()
                    delay(1000L)
                }
            }
        } else if (!hasPending) {
            pendingTickJob?.cancel()
            pendingTickJob = null
        }
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

    fun cancelOrder(id: Long) {
        changeStatus(id, ORDER_STATUS_CANCELED)
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
        val closed = updated.status == ORDER_STATUS_CANCELED ||
            updated.status == ORDER_STATUS_DELIVERED
        val order = orders.value
        if (order is ResultEntity.Success) {
            val list = order.data.map {
                if (it.id == updated.id) updated else it
            }.addHeaders()
            orders.postValue(ResultEntity.Success(list))
            if (!closed && updated.status >= ORDER_STATUS_IN_DELIVERY && list.any {
                    it.status < ORDER_STATUS_IN_DELIVERY
                }) {
                scrollUp.postValue(true)
            }
        }
        val current = currentOrder.value
        if (current?.id == updated.id) {
            currentOrder.postValue(updated)
            if (closed) {
                orderClosed.postValue(updated.id)
            }
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
        cartItemReadyEnabled.postValue(data.cartItemReadyEnabled)
        if (!data.cartItemReadyEnabled) {
            clearAllPendingItemStatuses()
        }
        allowEditOrder.postValue(data.allowEditOrder)
        enableManualPosExport.postValue(data.enableManualPosExport)
        showOrganization.postValue(data.showOrganization)
        commentMode.postValue(data.commentMode)
    }
}
