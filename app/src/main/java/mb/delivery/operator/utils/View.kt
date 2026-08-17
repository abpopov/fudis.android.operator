package mb.delivery.operator.utils

import android.widget.EditText

fun EditText.setTextCursor() {
    try {
        this.setSelection(this.text.length)
    } catch (e: Exception) {

    }
}