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
fun OrderEntity.toOrderStatusLabel(): Int {
    return when (status) {
        ORDER_STATUS_ACCEPTED -> R.string.order_status_queue
        ORDER_STATUS_IN_PROGRESS -> R.string.order_status_cooking
        ORDER_STATUS_READY -> R.string.order_status_ready
        ORDER_STATUS_IN_DELIVERY -> R.string.order_status_in_delivery
        ORDER_STATUS_DELIVERED -> if (isDeliveryOrder()) {
            R.string.order_status_delivered
        } else {
            R.string.order_status_completed
        }
        ORDER_STATUS_CANCELED -> R.string.order_status_canceled
        else -> R.string.order_status_new
    }
}

@StringRes
fun OrderEntity.toOrderActionLabel(): Int {
    return when (status) {
        ORDER_STATUS_ACCEPTED -> R.string.order_status_start
        ORDER_STATUS_IN_PROGRESS -> R.string.order_status_ready
        ORDER_STATUS_READY -> if (isDeliveryOrder()) {
            R.string.order_action_to_delivery
        } else {
            R.string.order_action_complete
        }
        ORDER_STATUS_IN_DELIVERY -> R.string.order_action_delivered
        else -> R.string.order_status_queue_2
    }
}

fun OrderEntity.hasOrderAction(): Boolean = getNextStatus() >= 0

@StringRes
fun courierServiceTypeLabel(type: Int?): Int? {
    return when (type) {
        COURIER_SERVICE_TYPE_YA_GO -> R.string.courier_service_type_yago
        COURIER_SERVICE_TYPE_ONESYNC -> R.string.courier_service_type_onesync
        else -> null
    }
}

@StringRes
fun courierServiceStatusLabel(status: Int?): Int? {
    return when (status) {
        COURIER_SERVICE_STATUS_COURIER_CANCELED -> R.string.courier_service_status_canceled_by_courier
        COURIER_SERVICE_STATUS_CANCELED -> R.string.courier_service_status_canceled
        COURIER_SERVICE_STATUS_CREATE_ERROR -> R.string.courier_service_status_create_error
        COURIER_SERVICE_STATUS_NOT_ENABLED -> R.string.courier_service_status_not_enabled
        COURIER_SERVICE_STATUS_COURIER_ERROR -> R.string.courier_service_status_error
        COURIER_SERVICE_STATUS_DRAFT -> R.string.courier_service_status_draft
        COURIER_SERVICE_STATUS_READY_FOR_APPROVAL -> R.string.courier_service_status_ready_for_approval
        COURIER_SERVICE_STATUS_SEARCH_COURIER -> R.string.courier_service_status_search
        COURIER_SERVICE_STATUS_COURIER_FOUND -> R.string.courier_service_status_found
        COURIER_SERVICE_STATUS_COURIER_NOT_FOUND -> R.string.courier_service_status_not_found
        COURIER_SERVICE_STATUS_COURIER_READY_FOR_PICK_UP -> R.string.courier_service_status_ready_pickup
        COURIER_SERVICE_STATUS_COURIER_RETURN -> R.string.courier_service_status_return
        COURIER_SERVICE_STATUS_COURIER_PICK_UP -> R.string.courier_service_status_pickup
        COURIER_SERVICE_STATUS_COURIER_DELIVERED -> R.string.courier_service_status_delivered
        else -> null
    }
}

fun List<OrderEntity>.addHeaders(): List<OrderEntity> {
    val result = this
        .filter {
            it.status != ORDER_STATUS_CANCELED && it.status != ORDER_STATUS_DELIVERED
        }
        .sortedByDescending { it.updatedAt ?: it.createdAt }
        .onEach {
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