package thapl.com.fudis.domain.case

import thapl.com.fudis.data.Repo
import thapl.com.fudis.domain.mapper.ProductListApiToEntityMapper
import thapl.com.fudis.domain.model.ProductEntity

class StopsUseCaseImpl(private val repo: Repo) : StopsUseCase {

    override fun getContext() = repo.getContext()

    override suspend fun getProducts() = with(repo.products(repo.getOrganizationId())) {
        ProductListApiToEntityMapper.map(this)
    }

    override suspend fun stopItem(id: Long, stop: Boolean): List<ProductEntity> {
        repo.stopProduct(repo.getOrganizationId(), id, stop)
        return getProducts()
    }

}