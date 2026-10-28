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
    val showHighload: Boolean?,
    @SerializedName("allow_status_without_dishes_ready")
    val allowStatusWithoutDishesReady: Boolean?,
    @SerializedName("cart_item_ready_enabled")
    val cartItemReadyEnabled: Boolean?,
    @SerializedName("allow_edit_order")
    val allowEditOrder: Boolean?,
    @SerializedName("enable_manual_pos_export")
    val enableManualPosExport: Boolean?,
    @SerializedName("show_organization")
    val showOrganization: Boolean?,
    @SerializedName("comment_mode")
    val commentMode: String?
) : Serializable
