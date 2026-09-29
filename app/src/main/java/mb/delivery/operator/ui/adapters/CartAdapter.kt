package mb.delivery.operator.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import mb.delivery.operator.GlideRequests
import mb.delivery.operator.R
import mb.delivery.operator.domain.model.CartEntity
import mb.delivery.operator.ui.base.BaseHolder
import mb.delivery.operator.ui.base.BaseListAdapter
import mb.delivery.operator.ui.orders.OrdersViewModel
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    glide: GlideRequests?,
    private val ordersViewModel: OrdersViewModel,
    click: (CartEntity, Any?) -> Unit
) :
    BaseListAdapter<OrdersViewModel, CartEntity, CartHolder>(
        glide,
        ordersViewModel,
        click,
        getHolder = { parent, _ ->
            CartHolder.from(parent)
        }
    ) {

    private val attachedHolders = mutableSetOf<CartHolder>()

    /** Смена pending (старт/отмена/commit) — полный rebind строки. */
    fun notifyPendingStateChanged() {
        notifyDataSetChanged()
    }

    /** Только обновление «через N с» — без rebind, чтобы не рвать клики. */
    fun refreshPendingCountdown() {
        attachedHolders.forEach { it.refreshPendingCountdown(ordersViewModel) }
    }

    override fun onViewAttachedToWindow(holder: CartHolder) {
        super.onViewAttachedToWindow(holder)
        attachedHolders.add(holder)
    }

    override fun onViewDetachedFromWindow(holder: CartHolder) {
        attachedHolders.remove(holder)
        super.onViewDetachedFromWindow(holder)
    }
}

class CartHolder(view: View) : BaseHolder<OrdersViewModel, CartEntity>(view) {

    private val textName = view.findViewById<TextView>(R.id.tvItemTitle)
    private val textPrice = view.findViewById<TextView>(R.id.tvItemPrice)
    private val btnMore = view.findViewById<TextView>(R.id.tvItemMore)
    private val btnStatus = view.findViewById<TextView>(R.id.tvItemStatus)
    private val btnAction = view.findViewById<TextView>(R.id.tvItemAction)
    private val listModifiers = view.findViewById<RecyclerView>(R.id.rvModifiersList)

    private var boundItemId: Int? = null
    private var boundActionLabelRes: Int? = null

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
        boundItemId = item.id
        textName.text = String.format(Locale.getDefault(), "%d × %s", item.count, item.item.baseTitle ?: item.item.title)
        textPrice.text = String.format("%s ₽", formatter.format((item.item.price.toDouble() + item.modifiers.sumOf {
            (it.count * it.modificator.price).toDouble()
        }) * item.count))

        val pending = viewModel.getPendingItemStatus(item.id)
        if (pending != null) {
            boundActionLabelRes = pending.actionLabelRes
            bindPending(item, pending.actionLabelRes, viewModel.pendingSecondsLeft(item.id), viewModel)
        } else {
            boundActionLabelRes = null
            bindStatusActions(item, viewModel)
        }

        btnMore.isInvisible = !item.hasTechCard
        btnMore.setOnClickListener {
            click(item, null)
        }
        val modifierAdapter = ModifierAdapter(glide, viewModel, click = { _, _ ->
        })
        listModifiers.adapter = modifierAdapter
        modifierAdapter.submitList(item.modifiers)
    }

    fun refreshPendingCountdown(viewModel: OrdersViewModel) {
        val itemId = boundItemId ?: return
        val actionRes = boundActionLabelRes ?: return
        if (viewModel.getPendingItemStatus(itemId) == null) return
        val actionName = itemView.context.getString(actionRes)
        btnStatus.text = itemView.context.getString(
            R.string.order_single_status_pending,
            actionName,
            viewModel.pendingSecondsLeft(itemId)
        )
    }

    private fun bindPending(
        item: CartEntity,
        actionLabelRes: Int,
        secondsLeft: Int,
        viewModel: OrdersViewModel
    ) {
        applyStatusChip(item.status)
        val actionName = itemView.context.getString(actionLabelRes)
        btnStatus.text = itemView.context.getString(
            R.string.order_single_status_pending,
            actionName,
            secondsLeft
        )
        btnStatus.isInvisible = false
        btnAction.text = itemView.context.getString(R.string.order_single_action_undo)
        btnAction.isInvisible = false
        btnAction.setOnClickListener {
            viewModel.cancelPendingItemStatus(item.id)
        }
    }

    private fun bindStatusActions(item: CartEntity, viewModel: OrdersViewModel) {
        when (item.status) {
            CartEntity.STATUS_NEW -> {
                applyStatusChip(item.status)
                btnStatus.isInvisible = false
                btnAction.isInvisible = true
                btnAction.setOnClickListener(null)
            }
            CartEntity.STATUS_READY_COOK -> {
                applyStatusChip(item.status)
                btnStatus.isInvisible = false
                btnAction.text = itemView.context.getString(R.string.order_single_action_start)
                btnAction.isInvisible = false
                btnAction.setOnClickListener {
                    viewModel.scheduleItemStatusChange(
                        item.id,
                        CartEntity.STATUS_COOKING,
                        R.string.order_single_action_start
                    )
                }
            }
            CartEntity.STATUS_COOKING -> {
                applyStatusChip(item.status)
                btnStatus.isInvisible = false
                btnAction.text = itemView.context.getString(R.string.order_single_action_ready)
                btnAction.isInvisible = false
                btnAction.setOnClickListener {
                    viewModel.scheduleItemStatusChange(
                        item.id,
                        CartEntity.STATUS_COOKED,
                        R.string.order_single_action_ready
                    )
                }
            }
            CartEntity.STATUS_COOKED -> {
                applyStatusChip(item.status)
                btnStatus.isInvisible = false
                btnAction.text = itemView.context.getString(R.string.order_single_action_done)
                btnAction.isInvisible = false
                btnAction.setOnClickListener {
                    viewModel.scheduleItemStatusChange(
                        item.id,
                        CartEntity.STATUS_DONE,
                        R.string.order_single_action_done
                    )
                }
            }
            CartEntity.STATUS_DONE -> {
                applyStatusChip(item.status)
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
    }

    private fun applyStatusChip(status: Int) {
        when (status) {
            CartEntity.STATUS_NEW -> {
                btnStatus.text = itemView.context.getString(R.string.order_single_status_new)
                btnStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                btnStatus.setBackgroundResource(R.drawable.ic_bg_rounded_red_8)
            }
            CartEntity.STATUS_READY_COOK -> {
                btnStatus.text = itemView.context.getString(R.string.order_single_status_ready)
                btnStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                btnStatus.setBackgroundResource(R.drawable.ic_bg_rounded_red_8)
            }
            CartEntity.STATUS_COOKING -> {
                btnStatus.text = itemView.context.getString(R.string.order_single_status_cooking)
                btnStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                btnStatus.setBackgroundResource(R.drawable.ic_bg_rounded_gold_8)
            }
            CartEntity.STATUS_COOKED -> {
                btnStatus.text = itemView.context.getString(R.string.order_single_status_cooked)
                btnStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                btnStatus.setBackgroundResource(R.drawable.ic_bg_rounded_green_8)
            }
            CartEntity.STATUS_DONE -> {
                btnStatus.text = itemView.context.getString(R.string.order_single_status_done)
                btnStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                btnStatus.setBackgroundResource(R.drawable.ic_bg_rounded_green_8)
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): CartHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_cart, parent, false)
            return CartHolder(view)
        }
    }
}
