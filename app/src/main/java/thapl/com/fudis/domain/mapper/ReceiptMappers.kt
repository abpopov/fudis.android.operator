package thapl.com.fudis.domain.mapper

import thapl.com.fudis.data.api.model.ReceiptApi
import thapl.com.fudis.data.api.model.ReceiptProductApi
import thapl.com.fudis.domain.model.ReceiptEntity
import thapl.com.fudis.domain.model.ReceiptProductEntity

object ReceiptApiToEntityMapper : BaseMapperNullable<ReceiptApi, ReceiptEntity> {

    override fun map(type: ReceiptApi?): ReceiptEntity? {
        type?.id ?: return null
        type.title ?: return null
        type.description ?: return null
        return ReceiptEntity(
            id = type.id,
            title = type.title,
            description = type.description,
            doneWeight = type.doneWeight,
            image = type.image,
            products = ReceiptProductListApiToEntityMapper.map(type.products)
        )
    }

}

object ReceiptProductListApiToEntityMapper : BaseMapperSafe<List<ReceiptProductApi>, List<ReceiptProductEntity>> {

    override fun map(type: List<ReceiptProductApi>?): List<ReceiptProductEntity> {
        return type?.mapNotNull { ReceiptProductApiToEntityMapper.map(it) } ?: listOf()
    }

}

object ReceiptProductApiToEntityMapper : BaseMapperNullable<ReceiptProductApi, ReceiptProductEntity> {

    override fun map(type: ReceiptProductApi?): ReceiptProductEntity? {
        type?.title ?: return null
        return ReceiptProductEntity(
            title = type.title,
            measure = type.measure,
            grossWeightPerItem = type.grossWeightPerItem,
            grossWeight = type.grossWeight,
            netWeight = type.netWeight,
            doneWeightWeight = type.doneWeightWeight
        )
    }

}