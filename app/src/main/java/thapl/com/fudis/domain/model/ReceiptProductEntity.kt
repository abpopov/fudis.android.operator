package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReceiptProductEntity(
    val title: String,
    val measure: String?,
    val grossWeightPerItem: String?,
    val grossWeight: String?,
    val netWeight: String?,
    val doneWeightWeight: String?
) : Parcelable