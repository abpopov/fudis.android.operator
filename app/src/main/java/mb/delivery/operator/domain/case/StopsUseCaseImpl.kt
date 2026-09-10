package mb.delivery.operator.domain.case

import mb.delivery.operator.data.Repo
import mb.delivery.operator.domain.mapper.OrganizationKitchenIndexApiToEntityMapper
import mb.delivery.operator.domain.mapper.ProductListApiToEntityMapper
import mb.delivery.operator.domain.mapper.StopListItemApiToEntityMapper
import mb.delivery.operator.domain.mapper.StopListResponseApiToEntityMapper
import mb.delivery.operator.domain.model.ErrorEntity
import mb.delivery.operator.domain.model.OrganizationKitchenIndex
import mb.delivery.operator.domain.model.ProductEntity
import mb.delivery.operator.domain.model.StopListItemEntity
import mb.delivery.operator.utils.FudisException

class StopsUseCaseImpl(private val repo: Repo) : StopsUseCase {

    override fun getContext() = repo.getContext()

    override fun getSavedOrganizationId() = repo.getOrganizationId()

    override fun saveOrganizationId(id: Int) {
        repo.setOrganizationId(id)
    }

    override suspend fun loadStopList(): List<StopListItemEntity> {
        return StopListResponseApiToEntityMapper.map(repo.stopList())
    }

    override suspend fun loadOrganizations(): OrganizationKitchenIndex {
        return OrganizationKitchenIndexApiToEntityMapper.map(repo.organizations())
            ?: throw FudisException(serverError())
    }

    override suspend fun loadProducts(organizationId: Int): List<ProductEntity> {
        return ProductListApiToEntityMapper.map(repo.products(organizationId).items)
    }

    override suspend fun setProductStop(
        organizationId: Int,
        catalogItemId: Long,
        stop: Boolean
    ): StopListItemEntity {
        return StopListItemApiToEntityMapper.map(
            repo.stopProduct(organizationId, catalogItemId, stop)
        ) ?: throw FudisException(serverError())
    }

    private fun serverError() = ErrorEntity(
        code = 777,
        textCode = "server",
        type = "FudisException",
        message = ""
    )
}
