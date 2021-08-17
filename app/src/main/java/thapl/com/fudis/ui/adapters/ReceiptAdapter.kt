package thapl.com.fudis.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import thapl.com.fudis.GlideRequests
import thapl.com.fudis.R
import thapl.com.fudis.domain.model.*
import thapl.com.fudis.ui.base.BaseHolder
import thapl.com.fudis.ui.base.BaseListAdapter
import thapl.com.fudis.ui.orders.OrdersViewModel

class ReceiptAdapter(
    glide: GlideRequests?,
    viewModel: OrdersViewModel,
    click: (ReceiptProductEntity, Any?) -> Unit
) :
    BaseListAdapter<OrdersViewModel, ReceiptProductEntity, ReceiptHolder>(
        glide,
        viewModel,
        click,
        getHolder = { parent, _ ->
            ReceiptHolder.from(parent)
        }
    )

class ReceiptHolder(view: View) : BaseHolder<OrdersViewModel, ReceiptProductEntity>(view) {

    private val textNumber = view.findViewById<TextView>(R.id.tvColumnNumber)
    private val textName = view.findViewById<TextView>(R.id.tvColumnTitle)
    private val textMeasure = view.findViewById<TextView>(R.id.tvColumnPiece)
    private val textPieceWeight = view.findViewById<TextView>(R.id.tvColumnPieceWeight)
    private val textGrossWeight = view.findViewById<TextView>(R.id.tvColumnGrossWeight)
    private val textNetWeight = view.findViewById<TextView>(R.id.tvColumnNetWeight)
    private val textReadyWeight = view.findViewById<TextView>(R.id.tvColumnReadyWeight)

    override fun bind(
        item: ReceiptProductEntity,
        position: Int,
        glide: GlideRequests?,
        viewModel: OrdersViewModel,
        isLast: Boolean,
        click: (ReceiptProductEntity, Any?) -> Unit
    ) {
        textName.text = item.title
        textMeasure.text = item.measure
        textPieceWeight.text = item.grossWeightPerItem
        textGrossWeight.text = item.grossWeight
        textNetWeight.text = item.netWeight
        textReadyWeight.text = item.doneWeightWeight
        textNumber.text = item.counter
    }

    companion object {
        fun from(parent: ViewGroup): ReceiptHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_receipt, parent, false)
            return ReceiptHolder(view)
        }
    }
}