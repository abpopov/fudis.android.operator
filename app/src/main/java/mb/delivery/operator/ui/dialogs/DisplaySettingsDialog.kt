package mb.delivery.operator.ui.dialogs

import android.content.DialogInterface
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import mb.delivery.operator.R
import mb.delivery.operator.utils.DisplayPrefs

class DisplaySettingsDialog : PanelDialogFragment() {

    override fun bodyLayoutRes() = R.layout.include_dialog_settings
    override fun titleRes() = R.string.settings_title

    private var startText = 1f
    private var startUi = 1f
    private var startDark = false
    private var handled = false

    private var preview: TextView? = null
    private var textLabel: TextView? = null
    private var btnTextMinus: TextView? = null
    private var btnTextPlus: TextView? = null
    private var uiLabel: TextView? = null
    private var btnUiMinus: TextView? = null
    private var btnUiPlus: TextView? = null
    private var btnTheme: TextView? = null
    private var btnLogout: TextView? = null

    override fun bindBody(root: View) {
        val ctx = requireContext()
        startText = DisplayPrefs.textScale(ctx)
        startUi = DisplayPrefs.uiScale(ctx)
        startDark = DisplayPrefs.isDark(ctx)

        preview = root.findViewById(R.id.tvPreview)
        textLabel = root.findViewById(R.id.tvTextScaleLabel)
        btnTextMinus = root.findViewById(R.id.btnTextMinus)
        btnTextPlus = root.findViewById(R.id.btnTextPlus)
        uiLabel = root.findViewById(R.id.tvUiScaleLabel)
        btnUiMinus = root.findViewById(R.id.btnUiMinus)
        btnUiPlus = root.findViewById(R.id.btnUiPlus)
        btnTheme = root.findViewById(R.id.btnTheme)
        btnLogout = root.findViewById(R.id.btnLogout)

        setPrimaryText(R.string.text_scale_close)
        setSecondaryVisible(false)

        btnTextMinus?.setOnClickListener {
            if (DisplayPrefs.decreaseText(ctx)) refresh()
        }
        btnTextPlus?.setOnClickListener {
            if (DisplayPrefs.increaseText(ctx)) refresh()
        }
        btnUiMinus?.setOnClickListener {
            if (DisplayPrefs.decreaseUi(ctx)) refresh()
        }
        btnUiPlus?.setOnClickListener {
            if (DisplayPrefs.increaseUi(ctx)) refresh()
        }
        btnTheme?.setOnClickListener {
            DisplayPrefs.toggleDark(ctx)
            refresh()
        }
        btnLogout?.setOnClickListener {
            LogoutConfirmDialog.show(parentFragmentManager)
        }
        refresh()
    }

    override fun onPrimaryClick() {
        dismissAllowingStateLoss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (handled) return
        handled = true
        val act = activity ?: return
        val changed = DisplayPrefs.textScale(act) != startText ||
            DisplayPrefs.uiScale(act) != startUi ||
            DisplayPrefs.isDark(act) != startDark
        if (changed) {
            act.recreate()
        }
    }

    private fun refresh() {
        val ctx = requireContext()
        textLabel?.text = DisplayPrefs.textLabel(ctx)
        btnTextMinus?.isEnabled = DisplayPrefs.canDecreaseText(ctx)
        btnTextPlus?.isEnabled = DisplayPrefs.canIncreaseText(ctx)
        btnTextMinus?.alpha = if (btnTextMinus?.isEnabled == true) 1f else 0.4f
        btnTextPlus?.alpha = if (btnTextPlus?.isEnabled == true) 1f else 0.4f

        uiLabel?.text = DisplayPrefs.uiLabel(ctx)
        btnUiMinus?.isEnabled = DisplayPrefs.canDecreaseUi(ctx)
        btnUiPlus?.isEnabled = DisplayPrefs.canIncreaseUi(ctx)
        btnUiMinus?.alpha = if (btnUiMinus?.isEnabled == true) 1f else 0.4f
        btnUiPlus?.alpha = if (btnUiPlus?.isEnabled == true) 1f else 0.4f

        btnTheme?.text = if (DisplayPrefs.isDark(ctx)) {
            getString(R.string.theme_dark)
        } else {
            getString(R.string.theme_light)
        }

        val sp = 24f * DisplayPrefs.textScale(ctx) * DisplayPrefs.uiScale(ctx)
        preview?.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            sp * resources.displayMetrics.density / resources.configuration.fontScale
        )
    }

    /** Не пересоздавать Activity при закрытии (например, перед logout). */
    fun skipRecreateOnDismiss() {
        handled = true
    }

    companion object {
        const val TAG = "DisplaySettingsDialog"

        fun show(fm: FragmentManager) {
            DisplaySettingsDialog().show(fm, TAG)
        }
    }
}
