package mb.delivery.operator.domain.case

import mb.delivery.operator.domain.model.CatalogEntity
import mb.delivery.operator.domain.model.CategoryEntity

interface CategoriesUseCase : BaseUseCase {
    suspend fun getCategories(): List<CategoryEntity>
    suspend fun getCatalog(id: Long?): List<CatalogEntity>
}