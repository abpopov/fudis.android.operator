package thapl.com.fudis.domain.mapper

import thapl.com.fudis.data.api.model.CartApi
import thapl.com.fudis.data.api.model.ConceptionApi
import thapl.com.fudis.data.api.model.OrderApi
import thapl.com.fudis.domain.model.*
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
            orderSource = type.orderSource ?: SOURCE_TYPE_SITE,
            status = type.status ?: ORDER_STATUS_NEW,
            organizationId = type.organizationId ?: 0,
            paymentType = type.paymentType ?: 0,
            paymentStatus = type.paymentStatus ?: 0,
            pointsNumber = type.pointsNumber ?: 0,
            orderSum = type.orderSum ?: 0f,
            paymentSum = type.paymentSum ?: 0f,
            discountSum = type.discountSum ?: 0f,
            dcOrderId = type.dcOrderId,
            address = type.address,
            entrance = type.entrance,
            floor = type.floor,
            flat = type.flat,
            doorCode = type.doorCode,
            phone = type.phone,
            personsCount = type.personsCount,
            clientComment = type.clientComment,
            operatorComment = type.operatorComment,
            lat = type.lat,
            lng = type.lng,
            createdAt = type.createdAt?.toTimestamp(),
            deliveryAt = type.deliveryAt?.toTimestamp(),
            updatedAt = type.updatedAt?.toTimestamp(),
            conception = ConceptionApiToEntityMapper.map(type.conception),
            cartData = CartListApiToEntityMapper.map(type.cartData)
        )
    }

}

object CartListApiToEntityMapper : BaseMapperSafe<List<CartApi>, List<CartEntity>> {

    override fun map(type: List<CartApi>?): List<CartEntity> {
        return type?.mapNotNull { CartApiToEntityMapper.map(it) } ?: listOf()
    }

}

object CartApiToEntityMapper : BaseMapperNullable<CartApi, CartEntity> {

    override fun map(type: CartApi?): CartEntity? {
        type?.title ?: return null
        type.id ?: return null
        return CartEntity(
            title = type.title,
            id = type.id,
            count = type.count ?: 0
        )
    }

}

object ConceptionApiToEntityMapper : BaseMapperNullable<ConceptionApi, ConceptionEntity> {

    override fun map(type: ConceptionApi?): ConceptionEntity? {
        type?.title ?: return null
        return ConceptionEntity(
            title = type.title,
            logo = type.img
        )
    }

}