package mb.delivery.operator.data.api.model

import com.google.gson.annotations.SerializedName

class AuthResultApi(
    @SerializedName("result")
    val result: Boolean?,
    @SerializedName("user")
    val user: UserApi?,
    @SerializedName("errors")
    val errors: Map<String, String>?
)
