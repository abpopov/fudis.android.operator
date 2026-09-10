package mb.delivery.operator.data.api.model

import com.google.gson.annotations.SerializedName

data class AuthRequestApi(
    @SerializedName("login")
    val login: String?,
    @SerializedName("password")
    val password: String?
)
