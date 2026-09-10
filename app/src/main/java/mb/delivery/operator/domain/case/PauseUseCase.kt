package mb.delivery.operator.domain.case

import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.OrganizationKitchenIndex

interface PauseUseCase : BaseUseCase {
    fun getSavedOrganizationId(): Int
    fun saveOrganizationId(id: Int)
    suspend fun load(): OrganizationKitchenIndex
    suspend fun setStatus(
        organizationId: Int,
        mode: String,
        cause: Int,
        dropAfterMinutes: Int,
        highLoadGap: Int?,
        stopCauseText: String?
    ): OrganizationKitchenEntity
    suspend fun dropStatus(organizationId: Int): OrganizationKitchenEntity
}
