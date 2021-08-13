package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReceiptProductApi(
    @SerializedName("title")
    val title: String?,
    @SerializedName("measure")
    val measure: String?,
    @SerializedName("gross_weight_per_item")
    val grossWeightPerItem: String?,
    @SerializedName("gross_weight")
    val grossWeight: String?,
    @SerializedName("net_weight")
    val netWeight: String?,
    @SerializedName("done_weight_weight")
    val doneWeightWeight: String?
) : Serializable