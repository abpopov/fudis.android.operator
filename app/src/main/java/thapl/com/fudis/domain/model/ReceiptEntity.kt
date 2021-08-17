package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReceiptEntity(
    val id: Long,
    val title: String,
    val description: String,
    val doneWeight: String?,
    val image: String?,
    val products: List<ReceiptProductEntity>
) : Parcelable