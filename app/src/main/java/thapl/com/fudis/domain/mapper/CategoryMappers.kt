package thapl.com.fudis.domain.mapper

import thapl.com.fudis.data.api.model.CategoryApi
import thapl.com.fudis.domain.model.CategoryEntity

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