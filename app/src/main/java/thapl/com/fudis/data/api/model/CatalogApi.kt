package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CatalogApi(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("ext_code")
    val extCode: String?,
    @SerializedName("active")
    val active: Boolean?,
    @SerializedName("created_at")
    val createdAt: String?
) : Serializable