package thapl.com.fudis.domain.case

import thapl.com.fudis.domain.model.CatalogEntity
import thapl.com.fudis.domain.model.CategoryEntity

interface CategoriesUseCase : BaseUseCase {
    suspend fun getCategories(): List<CategoryEntity>
    suspend fun getCatalog(id: Long?): List<CatalogEntity>
}