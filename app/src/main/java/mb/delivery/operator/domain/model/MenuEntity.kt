package mb.delivery.operator.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuEntity(
    val showMenu: Boolean,
    val showOrders: Boolean,
    val showStopList: Boolean,
    val showHighload: Boolean,
    val allowStatusWithoutDishesReady: Boolean,
    val allowEditOrder: Boolean,
    val enableManualPosExport: Boolean
) : Parcelable
