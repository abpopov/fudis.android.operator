package mb.delivery.operator.ui.dialogs

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import mb.delivery.operator.R
import mb.delivery.operator.domain.model.OrderEntity
import mb.delivery.operator.utils.courierServiceStatusLabel
import mb.delivery.operator.utils.courierServiceTypeLabel

class CourierInfoDialog : PanelDialogFragment() {

    override fun bodyLayoutRes() = R.layout.include_dialog_courier_info
    override fun titleRes() = R.string.courier_info_title

    override fun bindBody(root: View) {
        setPrimaryText(R.string.courier_info_close)
        setSecondaryVisible(false)

        val order = arguments?.getParcelable(ARG_ORDER) as? OrderEntity ?: run {
            dismissAllowingStateLoss()
            return
        }
        val data = order.courierServiceData

        bindPair(
            root.findViewById(R.id.tvCourierServiceLabel),
            root.findViewById(R.id.tvCourierServiceValue),
            serviceTitle(order.courierServiceType),
        )
        bindPair(
            root.findViewById(R.id.tvCourierStatusLabel),
            root.findViewById(R.id.tvCourierStatusValue),
            statusTitle(order.courierServiceStatus),
        )
        bindPair(
            root.findViewById(R.id.tvCourierNameLabel),
            root.findViewById(R.id.tvCourierNameValue),
            data?.name,
        )
        val phone = data?.phone?.takeIf { it.isNotBlank() }
            ?: order.courierServicePhone?.takeIf { it.isNotBlank() }
        val phoneText = when {
            phone == null -> null
            !data?.phoneExt.isNullOrBlank() ->
                "$phone ${getString(R.string.courier_info_phone_ext, data?.phoneExt)}"
            else -> phone
        }
        bindPair(
            root.findViewById(R.id.tvCourierPhoneLabel),
            root.findViewById(R.id.tvCourierPhoneValue),
            phoneText,
        )
        bindPair(
            root.findViewById(R.id.tvCourierCarModelLabel),
            root.findViewById(R.id.tvCourierCarModelValue),
            data?.carModel,
        )
        bindPair(
            root.findViewById(R.id.tvCourierCarNumberLabel),
            root.findViewById(R.id.tvCourierCarNumberValue),
            data?.carNumber,
        )
        bindPair(
            root.findViewById(R.id.tvCourierCarColorLabel),
            root.findViewById(R.id.tvCourierCarColorValue),
            data?.carColor,
        )
        bindPair(
            root.findViewById(R.id.tvCourierTransportLabel),
            root.findViewById(R.id.tvCourierTransportValue),
            data?.transportType,
        )
        val cost = data?.cost?.takeIf { it.isNotBlank() }
            ?: order.courierServiceCost?.takeIf { it.isNotBlank() }
        bindPair(
            root.findViewById(R.id.tvCourierCostLabel),
            root.findViewById(R.id.tvCourierCostValue),
            cost,
        )
        bindPair(
            root.findViewById(R.id.tvCourierTrackLabel),
            root.findViewById(R.id.tvCourierTrackValue),
            data?.trackLink,
        )
        bindPair(
            root.findViewById(R.id.tvCourierClaimLabel),
            root.findViewById(R.id.tvCourierClaimValue),
            order.courierServiceId,
        )
    }

    private fun serviceTitle(type: Int?): String? {
        type ?: return null
        val res = courierServiceTypeLabel(type)
        return if (res != null) getString(res) else getString(R.string.courier_service_type_unknown, type)
    }

    private fun statusTitle(status: Int?): String? {
        status ?: return null
        val res = courierServiceStatusLabel(status)
        return if (res != null) {
            getString(res)
        } else {
            getString(R.string.courier_service_status_unknown, status)
        }
    }

    private fun bindPair(label: AppCompatTextView, value: AppCompatTextView, text: String?) {
        val show = !text.isNullOrBlank()
        label.isVisible = show
        value.isVisible = show
        if (show) {
            value.text = text
        }
    }

    companion object {
        const val TAG = "CourierInfoDialog"
        private const val ARG_ORDER = "order"

        fun show(fm: FragmentManager, order: OrderEntity) {
            CourierInfoDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ORDER, order)
                }
            }.show(fm, TAG)
        }
    }
}
