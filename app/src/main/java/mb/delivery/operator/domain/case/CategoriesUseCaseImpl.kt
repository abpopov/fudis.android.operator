package mb.delivery.operator.domain.case

import mb.delivery.operator.data.Repo
import mb.delivery.operator.domain.mapper.CatalogListApiToEntityMapper
import mb.delivery.operator.domain.mapper.CategoryListApiToEntityMapper

class CategoriesUseCaseImpl(private val repo: Repo) : CategoriesUseCase {

    override fun getContext() = repo.getContext()

    override suspend fun getCategories() = with(repo.categories()) {
        CategoryListApiToEntityMapper.map(this)
    }

    override suspend fun getCatalog(id: Long?) = with(repo.catalog(id)) {
        CatalogListApiToEntityMapper.map(this)
    }
}