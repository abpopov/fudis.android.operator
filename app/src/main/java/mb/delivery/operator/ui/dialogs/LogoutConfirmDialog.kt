package mb.delivery.operator.ui.dialogs

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import mb.delivery.operator.R

class LogoutConfirmDialog : PanelDialogFragment() {

    override fun bodyLayoutRes() = R.layout.include_dialog_message
    override fun titleRes() = R.string.logout_confirm_title

    override fun bindBody(root: View) {
        setPrimaryText(R.string.logout_confirm_yes)
        setSecondaryVisible(true, R.string.order_edit_cancel)
    }

    override fun onPrimaryClick() {
        setFragmentResult(REQUEST_KEY, bundleOf(RESULT_CONFIRMED to true))
        dismissAllowingStateLoss()
    }

    companion object {
        const val TAG = "LogoutConfirmDialog"
        const val REQUEST_KEY = "logout_confirm_result"
        const val RESULT_CONFIRMED = "confirmed"

        fun show(fm: FragmentManager) {
            LogoutConfirmDialog().show(fm, TAG)
        }
    }
}
