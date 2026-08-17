package mb.delivery.operator.data.api.model

import java.io.Serializable

data class ErrorApi(
    val code: Int? = null,
    val textCode: String? = null,
    val type: String? = null,
    val message: String? = null
) : Serializable