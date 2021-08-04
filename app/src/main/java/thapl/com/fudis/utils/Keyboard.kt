package thapl.com.fudis.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun Context.showKeyboard(view: EditText?) {
    if (view == null) {
        return
    }
    view.requestFocus()
    view.setTextCursor()
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Context.hideKeyboard(view: View) {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.hideKeyboard(vararg view: View?) {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    view.forEach {
        it?.let { v ->
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }
}

fun Activity.hideKeyboard() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    val view = this.currentFocus ?: this.window.decorView
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}