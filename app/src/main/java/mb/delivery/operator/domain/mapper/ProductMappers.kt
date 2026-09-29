package mb.delivery.operator.domain.mapper

import mb.delivery.operator.data.api.model.CatalogProductsListApi
import mb.delivery.operator.data.api.model.ProductApi
import mb.delivery.operator.data.api.model.StopListItemApi
import mb.delivery.operator.data.api.model.StopListResponseApi
import mb.delivery.operator.data.api.model.StopProductCategoryApi
import mb.delivery.operator.domain.model.ProductEntity
import mb.delivery.operator.domain.model.StopListItemEntity
import mb.delivery.operator.domain.model.StopProductCategoryEntity
import mb.delivery.operator.domain.model.StopProductsIndex

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

object StopProductCategoryApiToEntityMapper : BaseMapperNullable<StopProductCategoryApi, StopProductCategoryEntity> {

    override fun map(type: StopProductCategoryApi?): StopProductCategoryEntity? {
        type ?: return null
        val id = type.id ?: return null
        val title = type.title ?: return null
        return StopProductCategoryEntity(
            id = id,
            title = title,
            items = ProductListApiToEntityMapper.map(type.items)
        )
    }

}

object CatalogProductsListApiToIndexMapper : BaseMapperSafe<CatalogProductsListApi, StopProductsIndex> {

    override fun map(type: CatalogProductsListApi?): StopProductsIndex {
        val categories = type?.categories?.mapNotNull { StopProductCategoryApiToEntityMapper.map(it) }
            .orEmpty()
        val flat = ProductListApiToEntityMapper.map(type?.items)
        val items = if (flat.isNotEmpty()) {
            flat
        } else {
            categories.flatMap { it.items }.distinctBy { it.id }
        }
        return StopProductsIndex(
            categories = categories.ifEmpty {
                if (items.isEmpty()) emptyList()
                else listOf(StopProductCategoryEntity(0, "Все блюда", items))
            },
            items = items
        )
    }

}

object StopListItemApiToEntityMapper : BaseMapperNullable<StopListItemApi, StopListItemEntity> {

    override fun map(type: StopListItemApi?): StopListItemEntity? {
        type?.catalogItemId ?: return null
        type.organizationId ?: return null
        return StopListItemEntity(
            catalogItemId = type.catalogItemId,
            title = type.title.orEmpty(),
            organizationId = type.organizationId,
            organizationTitle = type.organizationTitle.orEmpty(),
            isStop = type.isStop != false
        )
    }

}

object StopListResponseApiToEntityMapper : BaseMapperSafe<StopListResponseApi, List<StopListItemEntity>> {

    override fun map(type: StopListResponseApi?): List<StopListItemEntity> {
        return type?.items?.mapNotNull { StopListItemApiToEntityMapper.map(it) } ?: listOf()
    }

}
