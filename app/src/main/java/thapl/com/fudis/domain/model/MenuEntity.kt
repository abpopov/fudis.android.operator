package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuEntity(
    val showMenu: Boolean,
    val showOrders: Boolean,
    val showStopList: Boolean,
    val showHighload: Boolean
) : Parcelable