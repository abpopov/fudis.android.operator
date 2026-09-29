package mb.delivery.operator.domain.case

import mb.delivery.operator.domain.model.OrganizationKitchenIndex
import mb.delivery.operator.domain.model.StopListItemEntity
import mb.delivery.operator.domain.model.StopProductsIndex

interface StopsUseCase : BaseUseCase {
    fun getSavedOrganizationId(): Int
    fun saveOrganizationId(id: Int)
    suspend fun loadStopList(): List<StopListItemEntity>
    suspend fun loadOrganizations(): OrganizationKitchenIndex
    suspend fun loadProducts(organizationId: Int): StopProductsIndex
    suspend fun setProductStop(organizationId: Int, catalogItemId: Long, stop: Boolean): StopListItemEntity
}
