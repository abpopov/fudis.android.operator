package mb.delivery.operator.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrdersListApi(
    @SerializedName("items")
    val items: List<OrderApi>?
) : Serializable
