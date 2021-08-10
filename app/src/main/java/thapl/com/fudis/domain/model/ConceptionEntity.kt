package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConceptionEntity(
    val title: String,
    val logo: String?,
) : Parcelable