package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ModifierApi(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("modificator")
    val modificator: ModificatorApi?
) : Serializable