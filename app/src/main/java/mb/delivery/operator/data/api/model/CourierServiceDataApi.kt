package mb.delivery.operator.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CourierServiceDataApi(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("phone_ext")
    val phoneExt: String? = null,
    @SerializedName("car_model")
    val carModel: String? = null,
    @SerializedName("car_number")
    val carNumber: String? = null,
    @SerializedName("car_color")
    val carColor: String? = null,
    @SerializedName("transport_type")
    val transportType: String? = null,
    @SerializedName("cost")
    val cost: String? = null,
    @SerializedName("track_link")
    val trackLink: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
) : Serializable
