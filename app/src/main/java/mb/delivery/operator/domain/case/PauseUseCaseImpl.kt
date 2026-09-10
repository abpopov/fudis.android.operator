package mb.delivery.operator.domain.case

import mb.delivery.operator.data.Repo
import mb.delivery.operator.domain.mapper.OrganizationKitchenApiToEntityMapper
import mb.delivery.operator.domain.mapper.OrganizationKitchenIndexApiToEntityMapper
import mb.delivery.operator.domain.model.ErrorEntity
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.OrganizationKitchenIndex
import mb.delivery.operator.utils.FudisException

class PauseUseCaseImpl(private val repo: Repo) : PauseUseCase {

    override fun getContext() = repo.getContext()

    override fun getSavedOrganizationId() = repo.getOrganizationId()

    override fun saveOrganizationId(id: Int) {
        repo.setOrganizationId(id)
    }

    override suspend fun load(): OrganizationKitchenIndex {
        return OrganizationKitchenIndexApiToEntityMapper.map(repo.organizations())
            ?: throw FudisException(serverError())
    }

    override suspend fun setStatus(
        organizationId: Int,
        mode: String,
        cause: Int,
        dropAfterMinutes: Int,
        highLoadGap: Int?,
        stopCauseText: String?
    ): OrganizationKitchenEntity {
        return OrganizationKitchenApiToEntityMapper.map(
            repo.setOrganizationStatus(
                organizationId,
                mode,
                cause,
                dropAfterMinutes,
                highLoadGap,
                stopCauseText
            )
        ) ?: throw FudisException(serverError())
    }

    override suspend fun dropStatus(organizationId: Int): OrganizationKitchenEntity {
        return OrganizationKitchenApiToEntityMapper.map(repo.dropOrganizationStatus(organizationId))
            ?: throw FudisException(serverError())
    }

    private fun serverError() = ErrorEntity(
        code = 777,
        textCode = "server",
        type = "FudisException",
        message = ""
    )
}
