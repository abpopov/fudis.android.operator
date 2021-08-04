package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName

class AuthResultApi(
    @SerializedName("token")
    val token: String?,
    @SerializedName("user")
    val user: UserApi?,
    errorCode: Int?,
    errorMessage: String?
) : BaseApi(errorCode, errorMessage)