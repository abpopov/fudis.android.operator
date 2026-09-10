package mb.delivery.operator.domain.case

import mb.delivery.operator.data.api.model.EditProductApi
import mb.delivery.operator.data.api.model.OrderCartItemRequestApi
import mb.delivery.operator.domain.model.MenuEntity
import mb.delivery.operator.domain.model.OrderEntity
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.ReceiptEntity

interface OrdersUseCase : BaseUseCase {
    fun getShowMenu(): Boolean
    fun getShowOrders(): Boolean
    fun getShowStopList(): Boolean
    fun getShowHighload(): Boolean
    fun getAllowStatusWithoutDishesReady(): Boolean
    fun getAllowEditOrder(): Boolean
    fun getEnableManualPosExport(): Boolean
    suspend fun getOrders(): List<OrderEntity>
    suspend fun getReceipt(id: Long): ReceiptEntity
    suspend fun changeStatus(id: Long, status: Int): OrderEntity
    suspend fun changeItemStatus(item: Int, status: Int): OrderEntity
    suspend fun updateOrder(id: Long, clientComment: String?, organizationId: Int?): OrderEntity
    suspend fun updateCart(id: Long, cartItems: List<OrderCartItemRequestApi>): OrderEntity
    suspend fun exportToPos(id: Long): OrderEntity
    suspend fun editProducts(organizationId: Int): List<EditProductApi>
    suspend fun organizations(): List<OrganizationKitchenEntity>
    suspend fun getMenu(): MenuEntity
}
