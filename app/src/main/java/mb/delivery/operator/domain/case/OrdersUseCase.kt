package mb.delivery.operator.domain.case

import mb.delivery.operator.domain.model.MenuEntity
import mb.delivery.operator.domain.model.OrderEntity
import mb.delivery.operator.domain.model.ReceiptEntity

interface OrdersUseCase : BaseUseCase {
    fun getShowMenu(): Boolean
    fun getShowOrders(): Boolean
    fun getShowStopList(): Boolean
    fun getShowHighload(): Boolean
    suspend fun getOrders(): List<OrderEntity>
    suspend fun getReceipt(id: Long): ReceiptEntity
    suspend fun changeStatus(id: Long, status: Int): Pair<Long, Int>
    suspend fun changeItemStatus(item: Int, status: Int): Pair<Int, Int>
    suspend fun getMenu(): MenuEntity
}