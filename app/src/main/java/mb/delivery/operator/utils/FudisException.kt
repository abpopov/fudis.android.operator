package mb.delivery.operator.utils

import mb.delivery.operator.domain.model.ErrorEntity

data class FudisException(val error: ErrorEntity) : Exception(error.message)