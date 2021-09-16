package thapl.com.fudis.ui.adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import thapl.com.fudis.GlideRequests
import thapl.com.fudis.R
import thapl.com.fudis.domain.model.*
import thapl.com.fudis.ui.base.BaseHolder
import thapl.com.fudis.ui.base.BaseListAdapter
import thapl.com.fudis.ui.orders.OrdersViewModel
import thapl.com.fudis.utils.toOrderAction
import thapl.com.fudis.utils.toOrderStatus
import java.text.SimpleDateFormat
import java.util.*

const val MORE = 0
const val ACTION = 1

class OrdersAdapter(
    glide: GlideRequests?,
    viewModel: OrdersViewModel,
    click: (OrderEntity, Any?) -> Unit
) :
    BaseListAdapter<OrdersViewModel, OrderEntity, OrderHolder>(
        glide,
        viewModel,
        click,
        getHolder = { parent, _ ->
            OrderHolder.from(parent)
        }
    )

class OrderHolder(view: View) : BaseHolder<OrdersViewModel, OrderEntity>(view) {

    private val textHeader = view.findViewById<TextView>(R.id.tvHeader)
    private val textNumberOrder = view.findViewById<TextView>(R.id.tvNumberOrder)
    private val textVendorName = view.findViewById<TextView>(R.id.tvVendorName)
    private val textServiceName = view.findViewById<TextView>(R.id.tvServiceName)
    private val textOrderContent = view.findViewById<TextView>(R.id.tvOrderContent)
    private val textOrderTime = view.findViewById<TextView>(R.id.tvTimeValue)
    private val textOrderDue = view.findViewById<TextView>(R.id.tvDueValue)
    private val textOrderStatus = view.findViewById<TextView>(R.id.tvStatusValue)
    private val btnOrderMore = view.findViewById<TextView>(R.id.tvOrderMore)
    private val btnOrderAction = view.findViewById<TextView>(R.id.tvOrderAction)
    private val imgLogoVendor = view.findViewById<ImageView>(R.id.ivLogoVendor)
    private val imgLogoService = view.findViewById<ImageView>(R.id.ivLogoService)
    private val bgRoot = view.findViewById<View>(R.id.vRoot)

    override fun bind(
        item: OrderEntity,
        position: Int,
        glide: GlideRequests?,
        viewModel: OrdersViewModel,
        isLast: Boolean,
        click: (OrderEntity, Any?) -> Unit
    ) {
        textNumberOrder.text = String.format("№ %s", item.id.toString())
        item.dcOrderId?.let {
            textServiceName.text = String.format("№ %s", it)
        } ?: run {
            textServiceName.text = ""
        }
        when (item.orderSource) {
            SOURCE_TYPE_YA -> imgLogoService.setImageResource(R.drawable.ic_logo_yandex_eda)
            SOURCE_TYPE_DC -> imgLogoService.setImageResource(R.drawable.ic_logo_delivery_club)
            else -> imgLogoService.setImageResource(0)
        }
        textOrderContent.text = item.cartData.filter { it.count > 0 }
            .joinToString(separator = "  •  ") {
                "${it.count} × ${it.item.baseTitle ?: it.item.title}${
                    if (it.modifiers.isNotEmpty())
                        "(${it.modifiers.filter { m -> m.count > 0 }
                            .joinToString(separator = ", ") { me ->
                                me.modificator.title
                            }})"
                else 
                    ""
                }"
            }
        item.createdAt?.let { date ->
            textOrderTime.text = SimpleDateFormat(
                "dd.MM.yy ${itemView.context.getString(R.string.order_time_at)} HH:mm",
                Locale.getDefault()
            ).format(Date(date))
        } ?: run {
            textOrderTime.text = ""
        }
        item.deliveryAt?.let { date ->
            val dateObj = Date(date)
            textOrderDue.text = if (DateUtils.isToday(date)) {
                SimpleDateFormat(
                    "${itemView.context.getString(R.string.order_time_today)}, HH:mm",
                    Locale.getDefault()
                ).format(dateObj)
            } else {
                SimpleDateFormat("dd.MM.yy, HH:mm", Locale.getDefault()).format(dateObj)
            }
        } ?: run {
            textOrderDue.text = ""
        }

        item.conception?.let { c ->
            textVendorName.visibility = View.VISIBLE
            imgLogoVendor.visibility = View.VISIBLE
            textVendorName.text = c.title
            c.logo?.let {
                glide
                    ?.load(it)
                    ?.diskCacheStrategy(DiskCacheStrategy.ALL)
                    ?.circleCrop()
                    ?.fallback(0)
                    ?.into(imgLogoVendor)
            } ?: run {
                imgLogoVendor.setImageResource(0)
            }
        } ?: run {
            textVendorName.visibility = View.GONE
            imgLogoVendor.visibility = View.GONE
        }
        btnOrderMore.setOnClickListener {
            click(item, MORE)
        }
        btnOrderAction.setOnClickListener {
            btnOrderAction.isEnabled = false
            click(item, ACTION)
        }
        setChangeableData(item)
        viewModel.orders.observe(this, { orders ->
            if (orders is ResultEntity.Success) {
                val itemInList = orders.data.firstOrNull { it.id == item.id }
                itemInList?.let {
                    setChangeableData(it)
                }
            }
        })
    }

    private fun setChangeableData(item: OrderEntity) {
        val status = item.status.toOrderStatus()
        textOrderStatus.text = itemView.context.getString(status)
        bgRoot.setBackgroundResource(if (status == R.string.order_status_new) R.drawable.ic_bg_stroke_gr_32 else 0)
        val action = item.status.toOrderAction()
        if (action != 0) {
            btnOrderAction.isEnabled = true
            btnOrderAction.visibility = View.VISIBLE
            btnOrderAction.text = itemView.context.getString(action)
        } else {
            btnOrderAction.visibility = View.INVISIBLE
        }
        item.header?.let {
            textHeader.visibility = View.VISIBLE
            textHeader.text = itemView.context.getString(it)
        } ?: run {
            textHeader.visibility = View.GONE
        }
    }

    companion object {
        fun from(parent: ViewGroup): OrderHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_order, parent, false)
            return OrderHolder(view)
        }
    }
}