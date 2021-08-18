package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CartApi(
    @SerializedName("catalog_item")
    val catalogItem: CatalogItemApi?,
    @SerializedName("modifiers")
    val modifiers: List<ModifierApi>?,
    @SerializedName("count")
    val count: Int?
) : Serializable