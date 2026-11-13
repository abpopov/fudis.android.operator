package mb.delivery.operator.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val COURIER_SERVICE_TYPE_YA_GO = 10
const val COURIER_SERVICE_TYPE_ONESYNC = 40

const val COURIER_SERVICE_STATUS_COURIER_CANCELED = -30
const val COURIER_SERVICE_STATUS_CANCELED = -20
const val COURIER_SERVICE_STATUS_CREATE_ERROR = -5
const val COURIER_SERVICE_STATUS_NOT_ENABLED = 0
const val COURIER_SERVICE_STATUS_COURIER_ERROR = 5
const val COURIER_SERVICE_STATUS_DRAFT = 10
const val COURIER_SERVICE_STATUS_READY_FOR_APPROVAL = 15
const val COURIER_SERVICE_STATUS_SEARCH_COURIER = 20
const val COURIER_SERVICE_STATUS_COURIER_FOUND = 30
const val COURIER_SERVICE_STATUS_COURIER_NOT_FOUND = 35
const val COURIER_SERVICE_STATUS_COURIER_READY_FOR_PICK_UP = 37
const val COURIER_SERVICE_STATUS_COURIER_RETURN = 38
const val COURIER_SERVICE_STATUS_COURIER_PICK_UP = 40
const val COURIER_SERVICE_STATUS_COURIER_DELIVERED = 50

@Parcelize
data class CourierServiceDataEntity(
    val name: String? = null,
    val phone: String? = null,
    val phoneExt: String? = null,
    val carModel: String? = null,
    val carNumber: String? = null,
    val carColor: String? = null,
    val transportType: String? = null,
    val cost: String? = null,
    val trackLink: String? = null,
    val updatedAt: String? = null,
) : Parcelable {
    fun hasDisplayableFields(): Boolean =
        !name.isNullOrBlank()
            || !phone.isNullOrBlank()
            || !carModel.isNullOrBlank()
            || !carNumber.isNullOrBlank()
            || !carColor.isNullOrBlank()
            || !transportType.isNullOrBlank()
            || !cost.isNullOrBlank()
            || !trackLink.isNullOrBlank()
}
