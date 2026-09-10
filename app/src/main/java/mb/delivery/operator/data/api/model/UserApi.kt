package mb.delivery.operator.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserApi(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("accessToken")
    val accessToken: String?,
    @SerializedName("refreshToken")
    val refreshToken: String?,
    @SerializedName("organizations")
    val organizations: List<OrganizationApi>?,
    @SerializedName("ws_socket_url")
    val wsSocketUrl: String?,
    @SerializedName("ws_socket_channel")
    val wsSocketChannel: String?,
    @SerializedName("ws_socket_token")
    val wsSocketToken: String?
) : Serializable

data class OrganizationApi(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("title")
    val title: String?
) : Serializable
