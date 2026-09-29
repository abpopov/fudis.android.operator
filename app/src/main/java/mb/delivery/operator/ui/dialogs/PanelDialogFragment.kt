package mb.delivery.operator.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import mb.delivery.operator.R
import mb.delivery.operator.ui.base.BaseDialogFragment

/**
 * Полноэкранная кастомная панель (не системный AlertDialog).
 */
abstract class PanelDialogFragment : BaseDialogFragment() {

    protected var tvTitle: AppCompatTextView? = null
    protected var ivClose: AppCompatImageView? = null
    protected var vBody: FrameLayout? = null
    protected var btnPrimary: AppCompatTextView? = null
    protected var btnSecondary: AppCompatTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_dialog_panel, container, false)
        tvTitle = root.findViewById(R.id.tvTitle)
        ivClose = root.findViewById(R.id.ivClose)
        vBody = root.findViewById(R.id.vBody)
        btnPrimary = root.findViewById(R.id.btnPrimary)
        btnSecondary = root.findViewById(R.id.btnSecondary)
        inflater.inflate(bodyLayoutRes(), vBody, true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTitle?.setText(titleRes())
        ivClose?.setOnClickListener { dismissAllowingStateLoss() }
        btnPrimary?.setOnClickListener { onPrimaryClick() }
        btnSecondary?.setOnClickListener { onSecondaryClick() }
        bindBody(view)
    }

    protected abstract fun bodyLayoutRes(): Int
    protected abstract fun titleRes(): Int
    protected abstract fun bindBody(root: View)
    protected open fun onPrimaryClick() = dismissAllowingStateLoss()
    protected open fun onSecondaryClick() = dismissAllowingStateLoss()

    protected fun setSecondaryVisible(visible: Boolean, textRes: Int? = null) {
        btnSecondary?.isVisible = visible
        if (textRes != null) {
            btnSecondary?.setText(textRes)
        }
    }

    protected fun setPrimaryText(textRes: Int) {
        btnPrimary?.setText(textRes)
    }
}
