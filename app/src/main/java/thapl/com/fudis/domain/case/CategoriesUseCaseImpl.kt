package thapl.com.fudis.domain.case

import thapl.com.fudis.data.Repo
import thapl.com.fudis.domain.mapper.CatalogListApiToEntityMapper
import thapl.com.fudis.domain.mapper.CategoryListApiToEntityMapper

class CategoriesUseCaseImpl(private val repo: Repo) : CategoriesUseCase {

    override fun getContext() = repo.getContext()

    override suspend fun getCategories() = with(repo.categories()) {
        CategoryListApiToEntityMapper.map(this)
    }

    override suspend fun getCatalog(id: Long?) = with(repo.catalog(id)) {
        CatalogListApiToEntityMapper.map(this)
    }
}