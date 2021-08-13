package thapl.com.fudis.domain.mapper

import thapl.com.fudis.data.api.model.ProductApi
import thapl.com.fudis.domain.model.ProductEntity

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