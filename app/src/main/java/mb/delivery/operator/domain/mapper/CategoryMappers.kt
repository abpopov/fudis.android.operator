package mb.delivery.operator.domain.mapper

import mb.delivery.operator.data.api.model.CategoryApi
import mb.delivery.operator.domain.model.CategoryEntity

object CategoryListApiToEntityMapper : BaseMapperSafe<List<CategoryApi>, List<CategoryEntity>> {

    override fun map(type: List<CategoryApi>?): List<CategoryEntity> {
        return type?.mapNotNull { CategoryApiToEntityMapper.map(it) } ?: listOf()
    }

}

object CategoryApiToEntityMapper : BaseMapperNullable<CategoryApi, CategoryEntity> {

    override fun map(type: CategoryApi?): CategoryEntity? {
        type?.id ?: return null
        type.title ?: return null
        return CategoryEntity(
            id = type.id,
            title = type.title,
            subCategories = CategoryListApiToEntityMapper.map(type.subCategories),
            children = CatalogListApiToEntityMapper.map(type.catalogItems)
        )
    }

}