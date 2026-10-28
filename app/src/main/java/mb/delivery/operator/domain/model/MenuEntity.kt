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
    /** Кнопки готовности позиций (Готово/Отдано). Default true — как раньше. */
    val cartItemReadyEnabled: Boolean,
    val allowEditOrder: Boolean,
    val enableManualPosExport: Boolean,
    val showOrganization: Boolean,
    val commentMode: String
) : Parcelable
