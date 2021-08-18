package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModificatorEntity(
    val id: Long,
    val title: String,
    val price: Float,
) : Parcelable