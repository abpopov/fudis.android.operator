package mb.delivery.operator.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CartApi(
    @SerializedName("catalog_item")
    val catalogItem: CatalogItemApi?,
    @SerializedName("modifiers")
    val modifiers: List<ModifierApi>?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("cart_item_id")
    val id2: Int?,
    @SerializedName("count")
    val count: Int?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("has_tech_card")
    val hasTechCard: Boolean?
) : Serializable