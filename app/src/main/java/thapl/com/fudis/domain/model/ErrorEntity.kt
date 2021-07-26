package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorEntity(
    val code: Int,
    val textCode: String,
    val type: String,
    val message: String
) : Parcelable