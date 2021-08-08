package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CartApi(
    @SerializedName("titte")
    val title: String?,
    @SerializedName("id")
    val id: Long?,
    @SerializedName("count")
    val count: Int?
) : Serializable