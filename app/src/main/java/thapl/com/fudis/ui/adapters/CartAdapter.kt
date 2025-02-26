package thapl.com.fudis.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import thapl.com.fudis.GlideRequests
import thapl.com.fudis.R
import thapl.com.fudis.domain.model.CartEntity
import thapl.com.fudis.ui.base.BaseHolder
import thapl.com.fudis.ui.base.BaseListAdapter
import thapl.com.fudis.ui.orders.OrdersViewModel
import java.text.NumberFormat
import java.util.Locale

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
    private val btnStatus = view.findViewById<TextView>(R.id.tvItemStatus)
    private val btnAction = view.findViewById<TextView>(R.id.tvItemAction)
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
        textName.text = String.format(Locale.getDefault(), "%d × %s", item.count, item.item.baseTitle ?: item.item.title)
        textPrice.text = String.format("%s ₽", formatter.format((item.item.price.toDouble() + item.modifiers.sumOf {
            (it.count * it.modificator.price).toDouble()
        }) * item.count))
        when (item.status) {
            CartEntity.STATUS_NEW -> {
                btnStatus.text = itemView.context.getString(R.string.order_single_status_new)
                btnStatus.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.white)
                )
                btnStatus.setBackgroundResource(R.drawable.ic_bg_rounded_red_8)
                btnStatus.isInvisible = false
                btnAction.isInvisible = true
                btnAction.setOnClickListener(null)
            }
            CartEntity.STATUS_READY_COOK -> {
                btnStatus.text = itemView.context.getString(R.string.order_single_status_ready)
                btnStatus.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.white)
                )
                btnStatus.setBackgroundResource(R.drawable.ic_bg_rounded_red_8)
                btnStatus.isInvisible = false
                btnAction.text = itemView.context.getString(R.string.order_single_action_start)
                btnAction.isInvisible = false
                btnAction.setOnClickListener {
                    btnAction.isInvisible = true
                    viewModel.changeItemStatus(item.id, 5)
                }
            }
            CartEntity.STATUS_COOKING -> {
                btnStatus.text = itemView.context.getString(R.string.order_single_status_cooking)
                btnStatus.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.black)
                )
                btnStatus.setBackgroundResource(R.drawable.ic_bg_rounded_gold_8)
                btnStatus.isInvisible = false
                btnAction.text = itemView.context.getString(R.string.order_single_action_ready)
                btnAction.isInvisible = false
                btnAction.setOnClickListener {
                    btnAction.isInvisible = true
                    viewModel.changeItemStatus(item.id, 7)
                }
            }
            CartEntity.STATUS_COOKED -> {
                btnStatus.text = itemView.context.getString(R.string.order_single_status_cooked)
                btnStatus.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.black)
                )
                btnStatus.setBackgroundResource(R.drawable.ic_bg_rounded_green_8)
                btnStatus.isInvisible = false
                btnAction.text = itemView.context.getString(R.string.order_single_action_done)
                btnAction.isInvisible = false
                btnAction.setOnClickListener {
                    btnAction.isInvisible = true
                    viewModel.changeItemStatus(item.id, 10)
                }
            }
            CartEntity.STATUS_DONE -> {
                btnStatus.text = itemView.context.getString(R.string.order_single_status_done)
                btnStatus.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.black)
                )
                btnStatus.setBackgroundResource(R.drawable.ic_bg_rounded_green_8)
                btnStatus.isInvisible = false
                btnAction.isInvisible = true
                btnAction.setOnClickListener(null)
            }
            else -> {
                btnStatus.isInvisible = true
                btnAction.isInvisible = true
                btnAction.setOnClickListener(null)
            }
        }
        btnMore.isVisible = item.hasTechCard
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