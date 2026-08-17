package mb.delivery.operator.domain.case

import mb.delivery.operator.data.Repo
import mb.delivery.operator.domain.mapper.ProductListApiToEntityMapper
import mb.delivery.operator.domain.model.ProductEntity

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