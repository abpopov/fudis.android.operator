package thapl.com.fudis.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context?.call(phone: String) {
    this ?: return
    try {
        startActivity(Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        })
    } catch (e: Exception) {

    }
}