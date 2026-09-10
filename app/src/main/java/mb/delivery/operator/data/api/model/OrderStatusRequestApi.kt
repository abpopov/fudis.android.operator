package mb.delivery.operator.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderStatusRequestApi(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("status")
    val status: Int?
) : Serializable
