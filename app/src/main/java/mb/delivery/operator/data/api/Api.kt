package mb.delivery.operator.data.api

import mb.delivery.operator.data.api.model.*

interface Api {
    suspend fun auth(username: String?, password: String?): AuthResultApi
    suspend fun refreshSession(): UserApi
    suspend fun orders(): OrdersListApi
    suspend fun order(id: Long): OrderApi
    suspend fun changeStatus(order: Long?, status: Int?): OrderApi
    suspend fun changeItemStatus(item: Int?, status: Int?): OrderApi
    suspend fun updateOrder(id: Long, clientComment: String?, organizationId: Int?): OrderApi
    suspend fun updateCart(id: Long, cartItems: List<OrderCartItemRequestApi>): OrderApi
    suspend fun exportToPos(id: Long): OrderExportToPosResponseApi
    suspend fun editProducts(organizationId: Int): EditProductsListApi
    suspend fun menu(): MenuApi
    suspend fun categories(): List<CategoryApi>
    suspend fun catalog(id: Long?): List<CatalogApi>
    suspend fun products(organizationId: Int): CatalogProductsListApi
    suspend fun stopList(): StopListResponseApi
    suspend fun stopProduct(organizationId: Int, catalogItemId: Long, stop: Boolean): StopListItemApi
    suspend fun receipt(id: Long?): ReceiptApi
    suspend fun organizations(): OrganizationKitchenListApi
    suspend fun setOrganizationStatus(
        organizationId: Int,
        mode: String,
        cause: Int,
        dropAfterMinutes: Int,
        highLoadGap: Int?,
        stopCauseText: String?
    ): OrganizationKitchenApi
    suspend fun dropOrganizationStatus(organizationId: Int): OrganizationKitchenApi
}