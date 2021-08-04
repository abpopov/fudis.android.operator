package thapl.com.fudis.utils

import android.widget.EditText

fun EditText.setTextCursor() {
    try {
        this.setSelection(this.text.length)
    } catch (e: Exception) {

    }
}