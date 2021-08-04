package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val BAD_LOGIN = 10004

open class BaseApi(
    @SerializedName("error_code")
    val errorCode: Int?,
    @SerializedName("error_msg")
    val errorMessage: String?
) : Serializable