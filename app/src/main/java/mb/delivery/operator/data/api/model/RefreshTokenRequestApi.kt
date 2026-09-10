package mb.delivery.operator.data.api.model

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequestApi(
    @SerializedName("refreshToken")
    val refreshToken: String
)
