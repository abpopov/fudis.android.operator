package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ModificatorApi(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("price")
    val price: Float?
) : Serializable