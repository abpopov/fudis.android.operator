package thapl.com.fudis.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatTextView

class CheckableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), Checkable {

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }

    private var stateChecked = false
        set(value) {
            if (value != field) {
                field = value
                refreshDrawableState()
            }
        }

    override fun isChecked(): Boolean {
        return stateChecked
    }

    override fun setChecked(b: Boolean) {
        stateChecked = b
    }

    override fun toggle() {
        stateChecked = !stateChecked
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }
}