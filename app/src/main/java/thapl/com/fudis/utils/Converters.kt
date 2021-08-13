package thapl.com.fudis.utils

import androidx.annotation.StringRes
import thapl.com.fudis.R
import thapl.com.fudis.domain.model.*
import java.text.SimpleDateFormat
import java.util.*

fun String?.toTimestamp(): Long? {
    this ?: return null
    return try {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(this)?.time
    } catch (e: Exception) {
        null
    }
}

fun Long?.toDate(): Date? {
    this ?: return null
    return Date(this)
}

@StringRes
fun Int.toOrderStatus(): Int {
    return when (this) {
        ORDER_STATUS_IN_PROGRESS -> R.string.order_status_queue
        ORDER_STATUS_READY -> R.string.order_status_ready
        ORDER_STATUS_IN_DELIVERY -> R.string.order_status_delivery
        ORDER_STATUS_DELIVERED -> R.string.order_status_delivered
        else -> R.string.order_status_new
    }
}

@StringRes
fun Int.toOrderAction(): Int {
    return when (this) {
        ORDER_STATUS_IN_PROGRESS -> R.string.order_status_ready
        ORDER_STATUS_READY -> R.string.order_status_delivery
        ORDER_STATUS_IN_DELIVERY -> 0
        ORDER_STATUS_DELIVERED -> 0
        else -> R.string.order_status_queue
    }
}

fun Int?.toTimePause(): Int {
    return when (this) {
        R.id.rb15 -> 15
        R.id.rb30 -> 30
        R.id.rb60 -> 60
        else -> 0
    }
}

fun Int?.toCausePause(): Int {
    return when (this) {
        R.id.rbFood -> 20
        R.id.rbTime -> 10
        else -> 5
    }
}

fun List<OrderEntity>.addHeaders(): List<OrderEntity> {
    val result = this.sortedBy { it.createdAt }

    return mutableListOf<OrderEntity>().apply {
        addAll(result.filter { it.status < ORDER_STATUS_IN_DELIVERY }.also {
            it.getOrNull(0)?.header = R.string.order_active
        })
        addAll(result.filter { it.status >= ORDER_STATUS_IN_DELIVERY }.also {
            it.getOrNull(0)?.header = R.string.order_inactive
        })
    }
}