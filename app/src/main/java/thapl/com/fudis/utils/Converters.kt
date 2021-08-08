package thapl.com.fudis.utils

import java.text.SimpleDateFormat
import java.util.*

fun String?.toTimestamp(): Long? {
    this ?: return null
    return try {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(this)?.time
    } catch (e: Exception) {
        null
    }
}

fun Long?.toDate(): Date? {
    this ?: return null
    return Date(this)
}