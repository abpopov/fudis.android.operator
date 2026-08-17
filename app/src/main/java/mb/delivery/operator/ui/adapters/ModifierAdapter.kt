package mb.delivery.operator.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import mb.delivery.operator.GlideRequests
import mb.delivery.operator.R
import mb.delivery.operator.domain.model.ModifierEntity
import mb.delivery.operator.ui.base.BaseHolder
import mb.delivery.operator.ui.base.BaseListAdapter
import mb.delivery.operator.ui.orders.OrdersViewModel
import java.text.NumberFormat

class ModifierAdapter(
    glide: GlideRequests?,
    viewModel: OrdersViewModel,
    click: (ModifierEntity, Any?) -> Unit
) :
    BaseListAdapter<OrdersViewModel, ModifierEntity, ModifierHolder>(
        glide,
        viewModel,
        click,
        getHolder = { parent, _ ->
            ModifierHolder.from(parent)
        }
    )

class ModifierHolder(view: View) : BaseHolder<OrdersViewModel, ModifierEntity>(view) {

    private val textName = view.findViewById<TextView>(R.id.tvItemTitle)
    private val textPrice = view.findViewById<TextView>(R.id.tvItemPrice)

    private val formatter by lazy {
        NumberFormat.getNumberInstance().also {
            it.minimumFractionDigits = 0
            it.maximumFractionDigits = 2
        }
    }

    override fun bind(
        item: ModifierEntity,
        position: Int,
        glide: GlideRequests?,
        viewModel: OrdersViewModel,
        isLast: Boolean,
        click: (ModifierEntity, Any?) -> Unit
    ) {
        textName.text = item.modificator.title
        textPrice.text = String.format("+%s ₽", formatter.format((item.modificator.price.toDouble()) * item.count))
    }

    companion object {
        fun from(parent: ViewGroup): ModifierHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_product, parent, false)
            return ModifierHolder(view)
        }
    }
}