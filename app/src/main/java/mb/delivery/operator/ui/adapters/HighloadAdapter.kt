package mb.delivery.operator.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import mb.delivery.operator.GlideRequests
import mb.delivery.operator.R
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.ui.base.BaseHolder
import mb.delivery.operator.ui.base.BaseListAdapter
import mb.delivery.operator.ui.highload.HighloadViewModel

class HighloadAdapter(
    viewModel: HighloadViewModel,
    click: (OrganizationKitchenEntity, Any?) -> Unit
) : BaseListAdapter<HighloadViewModel, OrganizationKitchenEntity, HighloadHolder>(
    null,
    viewModel,
    click,
    getHolder = { parent, _ ->
        HighloadHolder.from(parent)
    }
)

class HighloadHolder(view: View) : BaseHolder<HighloadViewModel, OrganizationKitchenEntity>(view) {

    private val textName = view.findViewById<TextView>(R.id.tvItemName)
    private val textAddress = view.findViewById<TextView>(R.id.tvItemAddress)
    private val textStatus = view.findViewById<TextView>(R.id.tvItemStatus)
    private val textDrop = view.findViewById<TextView>(R.id.tvItemDrop)

    override fun bind(
        item: OrganizationKitchenEntity,
        position: Int,
        glide: GlideRequests?,
        viewModel: HighloadViewModel,
        isLast: Boolean,
        click: (OrganizationKitchenEntity, Any?) -> Unit
    ) {
        textName.text = item.label()
        val address = item.address?.takeIf { it.isNotBlank() && it != item.title }
        textAddress.isVisible = address != null
        textAddress.text = address
        textStatus.text = textStatus.context.getString(
            if (item.isTemporaryStopped) R.string.highload_status_stop else R.string.highload_status_highload
        )
        textStatus.setTextColor(
            ContextCompat.getColor(
                textStatus.context,
                if (item.isTemporaryStopped) R.color.red_status else R.color.gold_status
            )
        )
        textDrop.setOnClickListener { click(item, null) }
    }

    companion object {
        fun from(parent: ViewGroup): HighloadHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_highload, parent, false)
            return HighloadHolder(view)
        }
    }
}
