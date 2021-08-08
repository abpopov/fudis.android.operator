package thapl.com.fudis.domain.case

import thapl.com.fudis.domain.model.OrderEntity

interface OrdersUseCase : BaseUseCase {
    suspend fun getOrders(): List<OrderEntity>
}