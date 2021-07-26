package thapl.com.fudis.domain.mapper

import thapl.com.fudis.data.api.model.ErrorApi
import thapl.com.fudis.domain.model.ErrorEntity

object ErrorApiToEntityMapper : BaseMapperSafe<ErrorApi, ErrorEntity> {

    override fun map(type: ErrorApi?): ErrorEntity {
        return ErrorEntity(
            code = type?.code ?: 0,
            textCode = type?.textCode ?: "",
            type = type?.type ?: "",
            message = type?.message ?: ""
        )
    }

}