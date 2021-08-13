package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReceiptApi(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("done_weight")
    val doneWeight: String?,
    @SerializedName("img")
    val image: String?,
    @SerializedName("products")
    val products: List<ReceiptProductApi>?
) : Serializable