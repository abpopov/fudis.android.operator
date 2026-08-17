package mb.delivery.operator.domain.mapper

import mb.delivery.operator.data.api.model.ErrorApi
import mb.delivery.operator.domain.model.ErrorEntity

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