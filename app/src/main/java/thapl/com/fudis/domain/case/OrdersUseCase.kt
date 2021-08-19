package thapl.com.fudis.domain.case

import thapl.com.fudis.domain.model.OrderEntity
import thapl.com.fudis.domain.model.ReceiptEntity

interface OrdersUseCase : BaseUseCase {
    suspend fun getOrders(): List<OrderEntity>
    suspend fun getReceipt(id: Long): ReceiptEntity
    suspend fun changeStatus(id: Long, status: Int): Pair<Long, Int>
}