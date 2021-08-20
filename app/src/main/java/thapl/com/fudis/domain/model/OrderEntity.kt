package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val SOURCE_TYPE_SITE = 1
const val SOURCE_TYPE_DC = 3
const val SOURCE_TYPE_ZZ = 4
const val SOURCE_TYPE_YA = 5
const val SOURCE_TYPE_APP = 6

const val ORDER_STATUS_CANCELED = 5
const val ORDER_STATUS_NEW = 0
const val ORDER_STATUS_IN_PROGRESS = 10
const val ORDER_STATUS_READY = 15
const val ORDER_STATUS_IN_DELIVERY = 20
const val ORDER_STATUS_DELIVERED = 30

@Parcelize
data class OrderEntity(
    val id: Long,
    val conception: ConceptionEntity?,
    val cartData: List<CartEntity>,
    val orderSource: Int,
    val organizationId: Int,
    var status: Int,
    val paymentType: Int,
    val paymentStatus: Int,
    val pointsNumber: Int,
    val paymentSum: Float,
    val orderSum: Float,
    val discountSum: Float,
    val dcOrderId: String?,
    val personsCount: String?,
    val address: String?,
    val entrance: String?,
    val floor: String?,
    val flat: String?,
    val doorCode: String?,
    val phone: String?,
    val clientComment: String?,
    val operatorComment: String?,
    val lat: Double?,
    val lng: Double?,
    val createdAt: Long?,
    val deliveryAt: Long?,
    var updatedAt: Long?,
    var header: Int? = null
) : Parcelable, ListItem {

    fun getNextStatus(): Int {
        return when (status) {
            ORDER_STATUS_NEW -> ORDER_STATUS_IN_PROGRESS
            ORDER_STATUS_IN_PROGRESS -> ORDER_STATUS_READY
            ORDER_STATUS_READY -> ORDER_STATUS_IN_DELIVERY
            else -> -1
        }
    }

    override fun unique() = id

    override fun sameContent(other: ListItem) = this == other
}