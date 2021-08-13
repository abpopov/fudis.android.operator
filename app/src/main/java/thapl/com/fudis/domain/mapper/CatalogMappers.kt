package thapl.com.fudis.domain.mapper

import thapl.com.fudis.data.api.model.CatalogApi
import thapl.com.fudis.domain.model.CatalogEntity
import thapl.com.fudis.utils.toTimestamp

object CatalogListApiToEntityMapper : BaseMapperSafe<List<CatalogApi>, List<CatalogEntity>> {

    override fun map(type: List<CatalogApi>?): List<CatalogEntity> {
        return type?.mapNotNull { CatalogApiToEntityMapper.map(it) } ?: listOf()
    }

}

object CatalogApiToEntityMapper : BaseMapperNullable<CatalogApi, CatalogEntity> {

    override fun map(type: CatalogApi?): CatalogEntity? {
        type?.id ?: return null
        type.title ?: return null
        return CatalogEntity(
            id = type.id,
            title = type.title,
            isActive = type.active ?: false,
            extCode = type.extCode,
            createdAt = type.createdAt?.toTimestamp(),
        )
    }

}