package mb.delivery.operator.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MenuApi(
    @SerializedName("show_menu")
    val showMenu: Boolean?,
    @SerializedName("show_orders")
    val showOrders: Boolean?,
    @SerializedName("show_stop_list")
    val showStopList: Boolean?,
    @SerializedName("show_highload")
    val showHighload: Boolean?
) : Serializable
