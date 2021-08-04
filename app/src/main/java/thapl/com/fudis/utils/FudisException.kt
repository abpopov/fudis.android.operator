package thapl.com.fudis.utils

import thapl.com.fudis.domain.model.ErrorEntity

data class FudisException(val error: ErrorEntity) : Exception(error.message)