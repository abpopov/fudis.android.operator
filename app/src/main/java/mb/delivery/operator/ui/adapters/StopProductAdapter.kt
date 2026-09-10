package mb.delivery.operator.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import mb.delivery.operator.GlideRequests
import mb.delivery.operator.R
import mb.delivery.operator.domain.model.ProductEntity
import mb.delivery.operator.ui.base.BaseHolder
import mb.delivery.operator.ui.base.BaseListAdapter
import mb.delivery.operator.ui.stops.StopApplyViewModel

class StopProductAdapter(
    viewModel: StopApplyViewModel,
    click: (ProductEntity, Any?) -> Unit
) : BaseListAdapter<StopApplyViewModel, ProductEntity, StopProductHolder>(
    null,
    viewModel,
    click,
    getHolder = { parent, _ ->
        StopProductHolder.from(parent)
    }
)

class StopProductHolder(view: View) : BaseHolder<StopApplyViewModel, ProductEntity>(view) {

    private val textName = view.findViewById<TextView>(R.id.tvItemName)

    override fun bind(
        item: ProductEntity,
        position: Int,
        glide: GlideRequests?,
        viewModel: StopApplyViewModel,
        isLast: Boolean,
        click: (ProductEntity, Any?) -> Unit
    ) {
        textName.text = item.title
        itemView.setOnClickListener { click(item, null) }
    }

    companion object {
        fun from(parent: ViewGroup): StopProductHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_stop_product, parent, false)
            return StopProductHolder(view)
        }
    }
}
