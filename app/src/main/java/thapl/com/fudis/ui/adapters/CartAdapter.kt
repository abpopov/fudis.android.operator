package thapl.com.fudis.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import thapl.com.fudis.GlideRequests
import thapl.com.fudis.R
import thapl.com.fudis.domain.model.CartEntity
import thapl.com.fudis.ui.base.BaseHolder
import thapl.com.fudis.ui.base.BaseListAdapter
import thapl.com.fudis.ui.orders.OrdersViewModel
import java.text.NumberFormat

class CartAdapter(
    glide: GlideRequests?,
    viewModel: OrdersViewModel,
    click: (CartEntity, Any?) -> Unit
) :
    BaseListAdapter<OrdersViewModel, CartEntity, CartHolder>(
        glide,
        viewModel,
        click,
        getHolder = { parent, _ ->
            CartHolder.from(parent)
        }
    )

class CartHolder(view: View) : BaseHolder<OrdersViewModel, CartEntity>(view) {

    private val textName = view.findViewById<TextView>(R.id.tvItemTitle)
    private val textPrice = view.findViewById<TextView>(R.id.tvItemPrice)
    private val btnMore = view.findViewById<TextView>(R.id.tvItemMore)
    private val listModifiers = view.findViewById<RecyclerView>(R.id.rvModifiersList)

    private val formatter by lazy {
        NumberFormat.getNumberInstance().also {
            it.minimumFractionDigits = 0
            it.maximumFractionDigits = 2
        }
    }

    override fun bind(
        item: CartEntity,
        position: Int,
        glide: GlideRequests?,
        viewModel: OrdersViewModel,
        isLast: Boolean,
        click: (CartEntity, Any?) -> Unit
    ) {
        textName.text = String.format("%d × %s", item.count, item.item.title)
        textPrice.text = String.format("%s ₽", formatter.format((item.item.price.toDouble() + item.modifiers.sumOf {
            (it.count * it.modificator.price).toDouble()
        }) * item.count))
        btnMore.setOnClickListener {
            click(item, null)
        }
        val modifierAdapter = ModifierAdapter(glide, viewModel, click = { _, _ ->

        })
        listModifiers.adapter = modifierAdapter
        modifierAdapter.submitList(item.modifiers)
    }

    companion object {
        fun from(parent: ViewGroup): CartHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_cart, parent, false)
            return CartHolder(view)
        }
    }
}