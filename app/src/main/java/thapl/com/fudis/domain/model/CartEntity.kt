package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartEntity(
    val id: Long,
    val title: String,
    val count: Int
) : Parcelable