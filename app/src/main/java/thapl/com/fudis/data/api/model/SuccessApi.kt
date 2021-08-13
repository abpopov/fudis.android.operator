package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName

class SuccessApi(
    @SerializedName("success")
    val success: Boolean?,
    errorCode: Int?,
    errorMessage: String?
) : BaseApi(errorCode, errorMessage)