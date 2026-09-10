package mb.delivery.operator.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import mb.delivery.operator.GlideRequests
import mb.delivery.operator.R
import mb.delivery.operator.domain.model.StopListItemEntity
import mb.delivery.operator.ui.base.BaseHolder
import mb.delivery.operator.ui.base.BaseListAdapter
import mb.delivery.operator.ui.stops.StopsViewModel

class StopListAdapter(
    viewModel: StopsViewModel,
    click: (StopListItemEntity, Any?) -> Unit
) : BaseListAdapter<StopsViewModel, StopListItemEntity, StopListHolder>(
    null,
    viewModel,
    click,
    getHolder = { parent, _ ->
        StopListHolder.from(parent)
    }
)

class StopListHolder(view: View) : BaseHolder<StopsViewModel, StopListItemEntity>(view) {

    private val textName = view.findViewById<TextView>(R.id.tvItemName)
    private val textOrg = view.findViewById<TextView>(R.id.tvItemAddress)
    private val textStatus = view.findViewById<TextView>(R.id.tvItemStatus)
    private val textDrop = view.findViewById<TextView>(R.id.tvItemDrop)

    override fun bind(
        item: StopListItemEntity,
        position: Int,
        glide: GlideRequests?,
        viewModel: StopsViewModel,
        isLast: Boolean,
        click: (StopListItemEntity, Any?) -> Unit
    ) {
        textName.text = item.title
        textOrg.isVisible = false
        textStatus.text = textStatus.context.getString(R.string.highload_status_stop)
        textStatus.setTextColor(ContextCompat.getColor(textStatus.context, R.color.red_status))
        textDrop.setOnClickListener { click(item, null) }
    }

    companion object {
        fun from(parent: ViewGroup): StopListHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_highload, parent, false)
            return StopListHolder(view)
        }
    }
}
