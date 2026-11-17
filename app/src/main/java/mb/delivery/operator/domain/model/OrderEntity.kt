package mb.delivery.operator.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val SOURCE_TYPE_SITE = 1
const val SOURCE_TYPE_DC = 3
const val SOURCE_TYPE_ZZ = 4
const val SOURCE_TYPE_YA = 5
const val SOURCE_TYPE_APP = 6

const val ORDER_STATUS_CANCELED = 5
const val ORDER_STATUS_NEW = 0
const val ORDER_STATUS_ACCEPTED = 7
const val ORDER_STATUS_IN_PROGRESS = 10
const val ORDER_STATUS_READY = 15
const val ORDER_STATUS_IN_DELIVERY = 20
const val ORDER_STATUS_DELIVERED = 30

const val ORDER_TYPE_SELFSERVICE = 10
const val ORDER_TYPE_DELIVERY = 20
const val ORDER_TYPE_RESTAURANT = 30

const val PAYMENT_TYPE_CASH = 1
const val PAYMENT_TYPE_TERMINAL = 3

const val PAYMENT_STATUS_DEFAULT = 0
const val PAYMENT_STATUS_WAIT = 1
const val PAYMENT_STATUS_PAID = 2
const val PAYMENT_STATUS_BAD = 3
const val PAYMENT_STATUS_REFUNDED = 11
const val PAYMENT_STATUS_HOLDED = 20

@Parcelize
data class OrderEntity(
    val id: Long,
    val cartData: List<CartEntity>,
    val gift: CatalogItemEntity?,
    val orderSource: Int,
    /** 10 самовывоз, 20 доставка, 30 зал */
    val orderType: Int,
    val organizationId: Int,
    var status: Int,
    val paymentType: Int,
    val paymentStatus: Int,
    val pointsNumber: Int,
    val paymentSum: Float,
    val orderSum: Float,
    val discountSum: Float,
    val dcOrderId: String?,
    val personsCount: Int?,
    val address: String?,
    val entrance: String?,
    val floor: String?,
    val flat: String?,
    val doorCode: String?,
    val phone: String?,
    val clientComment: String?,
    val operatorComment: String?,
    val externalUuid: String?,
    val lat: Double?,
    val lng: Double?,
    val createdAt: Long?,
    val deliveryAt: Long?,
    var updatedAt: Long?,
    val courierServiceType: Int? = null,
    val courierServiceStatus: Int? = null,
    val courierServiceId: String? = null,
    val courierServicePhone: String? = null,
    val courierServiceCost: String? = null,
    val courierServiceData: CourierServiceDataEntity? = null,
    val hasExternalCourier: Boolean = false,
    var header: Int? = null
) : Parcelable, ListItem {

    fun isDeliveryOrder(): Boolean = orderType == ORDER_TYPE_DELIVERY

    fun hasExternalCourierInfo(): Boolean {
        if (hasExternalCourier) return true
        val type = courierServiceType ?: return false
        if (type != COURIER_SERVICE_TYPE_YA_GO && type != COURIER_SERVICE_TYPE_ONESYNC) {
            return false
        }
        val status = courierServiceStatus
        if (status != null && status != COURIER_SERVICE_STATUS_NOT_ENABLED) {
            return true
        }
        return courierServiceData?.hasDisplayableFields() == true
            || !courierServicePhone.isNullOrBlank()
            || !courierServiceCost.isNullOrBlank()
    }

    fun canCancel(): Boolean =
        status != ORDER_STATUS_CANCELED && status != ORDER_STATUS_DELIVERED

    fun getNextStatus(): Int {
        return when (status) {
            ORDER_STATUS_NEW -> ORDER_STATUS_ACCEPTED
            ORDER_STATUS_ACCEPTED -> ORDER_STATUS_IN_PROGRESS
            ORDER_STATUS_IN_PROGRESS -> ORDER_STATUS_READY
            ORDER_STATUS_READY -> if (isDeliveryOrder()) ORDER_STATUS_IN_DELIVERY else ORDER_STATUS_DELIVERED
            ORDER_STATUS_IN_DELIVERY -> ORDER_STATUS_DELIVERED
            else -> -1
        }
    }

    override fun unique() = id

    override fun sameContent(other: ListItem) = this == other

    fun itemsAreReady(): Boolean {
        return cartData.all {
            it.status >= CartEntity.STATUS_COOKED
        }
    }
}