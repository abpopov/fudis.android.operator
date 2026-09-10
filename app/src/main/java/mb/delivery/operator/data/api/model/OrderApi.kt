package mb.delivery.operator.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderApi(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("dc_order_id")
    val dcOrderId: String?,
    @SerializedName("order_source")
    val orderSource: Int?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("client_comment")
    val clientComment: String?,
    @SerializedName("persons_count")
    val personsCount: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("delivery_at")
    val deliveryAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("order_summ")
    val orderSum: Float?,
    @SerializedName("organization_id")
    val organizationId: Int?,
    @SerializedName("external_uuid")
    val externalUuid: String?,
    @SerializedName("gift")
    val gift: CatalogItemApi?,
    @SerializedName("cart_items")
    val cartItems: List<CartApi>?
) : Serializable
