package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConceptionApi(
    @SerializedName("title")
    val title: String?,
    @SerializedName("img")
    val img: String?
) : Serializable