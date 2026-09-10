package mb.delivery.operator.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CartApi(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("count")
    val count: Int?,
    @SerializedName("has_tech_card")
    val hasTechCard: Boolean?,
    @SerializedName("catalog_item")
    val catalogItem: CatalogItemApi?,
    @SerializedName("modifiers")
    val modifiers: List<ModifierApi>?
) : Serializable
