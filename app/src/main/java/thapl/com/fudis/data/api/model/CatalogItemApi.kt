package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CatalogItemApi(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("base_title")
    val baseTitle: String?,
    @SerializedName("base_price")
    val basePrice: Float?
) : Serializable