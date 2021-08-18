package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CartDataApi(
    @SerializedName("cartItems")
    val cartItems: List<CartApi>?
) : Serializable