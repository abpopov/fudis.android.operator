package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName

class StatusApi(
    @SerializedName("status")
    val status: Int?,
    errorCode: Int?,
    errorMessage: String?
) : BaseApi(errorCode, errorMessage)