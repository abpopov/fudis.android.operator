package mb.delivery.operator.domain.mapper

import mb.delivery.operator.data.api.model.ProductApi
import mb.delivery.operator.domain.model.ProductEntity

object ProductListApiToEntityMapper : BaseMapperSafe<List<ProductApi>, List<ProductEntity>> {

    override fun map(type: List<ProductApi>?): List<ProductEntity> {
        return type?.mapNotNull { ProductApiToEntityMapper.map(it) } ?: listOf()
    }

}

object ProductApiToEntityMapper : BaseMapperNullable<ProductApi, ProductEntity> {

    override fun map(type: ProductApi?): ProductEntity? {
        type?.id ?: return null
        type.title ?: return null
        return ProductEntity(
            id = type.id,
            title = type.title,
            isStopped = type.isStopped ?: false
        )
    }

}