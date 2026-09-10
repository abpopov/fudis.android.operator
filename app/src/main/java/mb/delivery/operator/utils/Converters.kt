package mb.delivery.operator.utils

import android.util.Log
import androidx.annotation.StringRes
import mb.delivery.operator.R
import mb.delivery.operator.domain.model.*
import java.text.SimpleDateFormat
import java.util.*

const val DATE_PATTERN = "yyyy-MM-dd HH:mm:ss"
const val DATE_Z_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ"

fun String?.toTimestamp(withZone: Boolean = false): Long? {
    this ?: return null
    return try {
        if (withZone) {
            SimpleDateFormat(DATE_Z_PATTERN, Locale.getDefault()).parse(this)?.time
        } else {
            SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).parse(this)?.time
        }
    } catch (e: Exception) {
        Log.e("okh", "parse $e")
        null
    }
}

fun Long?.toDateString(): String? {
    this ?: return null
    return SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(Date(this))
}

fun Long?.toDate(): Date? {
    this ?: return null
    return Date(this)
}

@StringRes
fun Int.toOrderStatus(): Int {
    return when (this) {
        ORDER_STATUS_ACCEPTED -> R.string.order_status_queue
        ORDER_STATUS_IN_PROGRESS -> R.string.order_status_cooking
        ORDER_STATUS_READY -> R.string.order_status_ready
        ORDER_STATUS_IN_DELIVERY -> R.string.order_status_delivery
        ORDER_STATUS_DELIVERED -> R.string.order_status_delivered
        else -> R.string.order_status_new
    }
}

@StringRes
fun Int.toOrderAction(): Int {
    return when (this) {
        ORDER_STATUS_ACCEPTED -> R.string.order_status_start
        ORDER_STATUS_IN_PROGRESS -> R.string.order_status_ready
        ORDER_STATUS_READY -> R.string.order_status_delivery
        ORDER_STATUS_IN_DELIVERY -> 0
        ORDER_STATUS_DELIVERED -> 0
        else -> R.string.order_status_queue_2
    }
}

fun List<OrderEntity>.addHeaders(): List<OrderEntity> {
    val result = this.sortedByDescending { it.updatedAt ?: it.createdAt }.onEach {
        it.header = null
    }

    return mutableListOf<OrderEntity>().apply {
        addAll(result.filter { it.status < ORDER_STATUS_IN_DELIVERY }.also {
            it.getOrNull(0)?.header = R.string.order_active
        })
        addAll(result.filter { it.status >= ORDER_STATUS_IN_DELIVERY }.also {
            it.getOrNull(0)?.header = R.string.order_inactive
        })
    }
}