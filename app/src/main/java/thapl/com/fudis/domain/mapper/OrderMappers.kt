package thapl.com.fudis.domain.mapper

import thapl.com.fudis.data.api.model.OrderApi
import thapl.com.fudis.domain.model.OrderEntity
import thapl.com.fudis.utils.toTimestamp

object OrderListApiToEntityMapper : BaseMapperSafe<List<OrderApi>, List<OrderEntity>> {

    override fun map(type: List<OrderApi>?): List<OrderEntity> {
        return type?.mapNotNull { OrderApiToEntityMapper.map(it) } ?: listOf()
    }

}

object OrderApiToEntityMapper : BaseMapperNullable<OrderApi, OrderEntity> {

    override fun map(type: OrderApi?): OrderEntity? {
        type?.id ?: return null
        return OrderEntity(
            id = type.id,
            createdAt = type.createdAt?.toTimestamp(),
            deliveryAt = type.deliveryAt?.toTimestamp(),
            updatedAt = type.updatedAt?.toTimestamp()
        )
    }

}