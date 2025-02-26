package thapl.com.fudis.domain.case

import thapl.com.fudis.domain.model.MenuEntity
import thapl.com.fudis.domain.model.OrderEntity
import thapl.com.fudis.domain.model.ReceiptEntity

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