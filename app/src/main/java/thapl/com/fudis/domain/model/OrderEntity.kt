package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderEntity(
    val id: Long,
    val createdAt: Long?,
    val deliveryAt: Long?,
    val updatedAt: Long?
) : Parcelable