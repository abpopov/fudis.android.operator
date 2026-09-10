package mb.delivery.operator.domain.mapper

import mb.delivery.operator.data.api.model.KitchenCauseApi
import mb.delivery.operator.data.api.model.KitchenTimeApi
import mb.delivery.operator.data.api.model.OrganizationKitchenApi
import mb.delivery.operator.data.api.model.OrganizationKitchenListApi
import mb.delivery.operator.domain.model.KitchenOption
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.OrganizationKitchenIndex

object OrganizationKitchenApiToEntityMapper : BaseMapperNullable<OrganizationKitchenApi, OrganizationKitchenEntity> {

    override fun map(type: OrganizationKitchenApi?): OrganizationKitchenEntity? {
        type?.id ?: return null
        return OrganizationKitchenEntity(
            id = type.id,
            title = type.title.orEmpty(),
            address = type.address,
            isTemporaryStopped = type.isTemporaryStopped == true,
            isHighLoadState = type.isHighLoadState == true,
            cause = type.cause,
            highLoadGap = type.highLoadGap,
            highLoadDropAfterTime = type.highLoadDropAfterTime
        )
    }

}

object OrganizationKitchenIndexApiToEntityMapper : BaseMapperNullable<OrganizationKitchenListApi, OrganizationKitchenIndex> {

    override fun map(type: OrganizationKitchenListApi?): OrganizationKitchenIndex? {
        type ?: return null
        return OrganizationKitchenIndex(
            items = type.items?.mapNotNull { OrganizationKitchenApiToEntityMapper.map(it) } ?: listOf(),
            causes = type.causes?.mapNotNull { it.toOption() } ?: listOf(),
            stopTimes = type.stopTimes?.mapNotNull { it.toOption() } ?: listOf(),
            highloadTimes = type.highloadTimes?.mapNotNull { it.toOption() } ?: listOf(),
            highloadAddTimes = type.highloadAddTimes?.mapNotNull { it.toOption() } ?: listOf()
        )
    }

    private fun KitchenCauseApi.toOption(): KitchenOption? {
        id ?: return null
        title ?: return null
        return KitchenOption(id, title)
    }

    private fun KitchenTimeApi.toOption(): KitchenOption? {
        minutes ?: return null
        title ?: return null
        return KitchenOption(minutes, title)
    }

}
