package mb.delivery.operator.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import mb.delivery.operator.R
import mb.delivery.operator.ui.base.BaseDialogFragment

/** Компактное подтверждение отмены заказа (не полноэкранная панель). */
class CancelOrderConfirmDialog : BaseDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dialog_confirm, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<AppCompatTextView>(R.id.tvTitle)?.setText(R.string.order_cancel_title)
        view.findViewById<AppCompatTextView>(R.id.tvMessage)?.setText(R.string.order_cancel_message)
        view.findViewById<View>(R.id.btnSecondary)?.setOnClickListener { dismissAllowingStateLoss() }
        view.findViewById<View>(R.id.btnPrimary)?.setOnClickListener {
            setFragmentResult(REQUEST_KEY, bundleOf(RESULT_CONFIRMED to true))
            dismissAllowingStateLoss()
        }
        view.setOnClickListener { dismissAllowingStateLoss() }
        view.findViewById<View>(R.id.vPanel)?.setOnClickListener { /* consume */ }
    }

    companion object {
        const val TAG = "CancelOrderConfirmDialog"
        const val REQUEST_KEY = "cancel_order_confirm_result"
        const val RESULT_CONFIRMED = "confirmed"

        fun show(fm: FragmentManager) {
            CancelOrderConfirmDialog().show(fm, TAG)
        }
    }
}
