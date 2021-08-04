package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RoleApi(
    @SerializedName("type")
    val type: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("ruleName")
    val ruleName: String?,
    @SerializedName("createdAt")
    val createdAt: Long?,
    @SerializedName("updatedAt")
    val updatedAt: Long?
) : Serializable