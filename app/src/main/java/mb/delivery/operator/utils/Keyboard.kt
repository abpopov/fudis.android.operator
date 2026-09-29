package mb.delivery.operator.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

/**
 * Поднимает контент над IME (нужно при translucent status, где adjustResize часто не работает).
 */
fun View.liftContentAboveIme() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
        if (view.paddingBottom != imeBottom) {
            view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, imeBottom)
        }
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

/** После появления клавиатуры сдвигает окно к сфокусированному полю. */
fun View.keepVisibleAboveIme() {
    setOnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) return@setOnFocusChangeListener
        v.post {
            val extra = (32 * v.resources.displayMetrics.density).toInt()
            val rect = Rect(0, 0, v.width, v.height + extra)
            v.requestRectangleOnScreen(rect, true)
        }
    }
}
