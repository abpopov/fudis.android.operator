package mb.delivery.operator.domain.mapper

import mb.delivery.operator.data.api.model.*
import mb.delivery.operator.domain.model.*
import mb.delivery.operator.utils.toTimestamp

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
            paymentType = 0,
            paymentStatus = 0,
            pointsNumber = 0,
            orderSum = type.orderSum ?: 0f,
            paymentSum = 0f,
            discountSum = 0f,
            dcOrderId = type.dcOrderId,
            address = null,
            entrance = null,
            floor = null,
            flat = null,
            doorCode = null,
            phone = null,
            personsCount = type.personsCount?.toIntOrNull(),
            clientComment = type.clientComment,
            operatorComment = null,
            externalUuid = type.externalUuid,
            lat = null,
            lng = null,
            createdAt = type.createdAt?.toTimestamp(),
            deliveryAt = type.deliveryAt?.toTimestamp(),
            updatedAt = type.updatedAt?.toTimestamp(),
            cartData = CartListApiToEntityMapper.map(type.cartItems),
            gift = CatalogItemApiToEntityMapper.map(type.gift)
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
        val item = CatalogItemApiToEntityMapper.map(type?.catalogItem)
        item ?: return null
        return CartEntity(
            item = item,
            modifiers = ModifierListApiToEntityMapper.map(type?.modifiers),
            id = type?.id ?: 0,
            count = type?.count ?: 0,
            status = type?.status ?: 0,
            hasTechCard = type?.hasTechCard ?: false
        )
    }

}

object ModifierListApiToEntityMapper : BaseMapperSafe<List<ModifierApi>, List<ModifierEntity>> {

    override fun map(type: List<ModifierApi>?): List<ModifierEntity> {
        return type?.mapNotNull { ModifierApiToEntityMapper.map(it) } ?: listOf()
    }

}

object CatalogItemApiToEntityMapper : BaseMapperNullable<CatalogItemApi, CatalogItemEntity> {

    override fun map(type: CatalogItemApi?): CatalogItemEntity? {
        type?.id ?: return null
        type.title ?: return null
        return CatalogItemEntity(
            title = type.title,
            baseTitle = type.baseTitle.takeIf { it.isNullOrEmpty().not() },
            id = type.id,
            price = type.basePrice ?: 0f
        )
    }

}

object ModifierApiToEntityMapper : BaseMapperNullable<ModifierApi, ModifierEntity> {

    override fun map(type: ModifierApi?): ModifierEntity? {
        val modificator = ModificatorApiToEntityMapper.map(type?.modificator)
        modificator ?: return null
        return ModifierEntity(
            count = type?.count ?: 0,
            modificator = modificator
        )
    }

}

object ModificatorApiToEntityMapper : BaseMapperNullable<ModificatorApi, ModificatorEntity> {

    override fun map(type: ModificatorApi?): ModificatorEntity? {
        type?.id ?: return null
        type.title ?: return null
        return ModificatorEntity(
            title = type.title,
            id = type.id,
            price = type.price ?: 0f
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