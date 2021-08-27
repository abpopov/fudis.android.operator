package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserApi(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("roles")
    val roles: Map<String, RoleApi>?,
    @SerializedName("organizations")
    val organizations: List<Int>?
) : Serializable