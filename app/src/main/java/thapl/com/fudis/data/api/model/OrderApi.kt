package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderApi(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("order_source")
    val orderSource: Int?,
    @SerializedName("organization_id")
    val organizationId: Int?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("payment_type")
    val paymentType: Int?,
    @SerializedName("payment_status")
    val paymentStatus: Int?,
    @SerializedName("points_number")
    val pointsNumber: Int?,
    @SerializedName("payment_summ")
    val paymentSum: Float?,
    @SerializedName("order_summ")
    val orderSum: Float?,
    @SerializedName("discount_sum")
    val discountSum: Float?,
    @SerializedName("conception")
    val conception: ConceptionApi?,
    @SerializedName("dc_order_id")
    val dcOrderId: String?,
    @SerializedName("persons_count")
    val personsCount: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("entrance")
    val entrance: String?,
    @SerializedName("floor")
    val floor: String?,
    @SerializedName("flat")
    val flat: String?,
    @SerializedName("doorcode")
    val doorCode: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("client_comment")
    val clientComment: String?,
    @SerializedName("operator_comment")
    val operatorComment: String?,
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lng")
    val lng: Double?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("delivery_at")
    val deliveryAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("cart_data")
    val cartData: List<CartApi>?
) : Serializable

//{"discount":null,"promo_id":null,"external_uuid":null]}