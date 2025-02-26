package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StatusRequestApi(
    @SerializedName("cart_item_id")
    val id: Int?,
    @SerializedName("status")
    val status: Int?
) : Serializable
