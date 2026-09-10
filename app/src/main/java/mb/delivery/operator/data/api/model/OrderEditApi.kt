package mb.delivery.operator.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderUpdateRequestApi(
    @SerializedName("id")
    val id: Long,
    @SerializedName("client_comment")
    val clientComment: String? = null,
    @SerializedName("organization_id")
    val organizationId: Int? = null
) : Serializable

data class OrderCartUpdateRequestApi(
    @SerializedName("id")
    val id: Long,
    @SerializedName("cart_items")
    val cartItems: List<OrderCartItemRequestApi>
) : Serializable

data class OrderCartItemRequestApi(
    @SerializedName("catalog_item_id")
    val catalogItemId: Long,
    @SerializedName("count")
    val count: Int,
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("modificators")
    val modificators: List<OrderCartModificatorRequestApi>? = null
) : Serializable

data class OrderCartModificatorRequestApi(
    @SerializedName("modificator_id")
    val modificatorId: Long,
    @SerializedName("count")
    val count: Int
) : Serializable

data class OrderExportToPosRequestApi(
    @SerializedName("id")
    val id: Long
) : Serializable

data class OrderExportToPosResponseApi(
    @SerializedName("queued")
    val queued: Boolean?,
    @SerializedName("order_id")
    val orderId: Long?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("order")
    val order: OrderApi?
) : Serializable

data class EditProductApi(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("base_title")
    val baseTitle: String?,
    @SerializedName("base_price")
    val basePrice: Float?,
    @SerializedName("modificators")
    val modificators: List<EditModificatorApi>?
) : Serializable

data class EditModificatorApi(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("price")
    val price: Float?
) : Serializable

data class EditProductsListApi(
    @SerializedName("items")
    val items: List<EditProductApi>?
) : Serializable
