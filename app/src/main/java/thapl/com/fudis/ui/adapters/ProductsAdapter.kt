package thapl.com.fudis.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import thapl.com.fudis.GlideRequests
import thapl.com.fudis.R
import thapl.com.fudis.domain.model.*
import thapl.com.fudis.ui.base.BaseHolder
import thapl.com.fudis.ui.base.BaseListAdapter
import thapl.com.fudis.ui.stops.StopsViewModel

class ProductsAdapter(
    glide: GlideRequests?,
    viewModel: StopsViewModel,
    click: (ProductEntity, Any?) -> Unit
) :
    BaseListAdapter<StopsViewModel, ProductEntity, ProductHolder>(
        glide,
        viewModel,
        click,
        getHolder = { parent, _ ->
            ProductHolder.from(parent)
        }
    )

class ProductHolder(view: View) : BaseHolder<StopsViewModel, ProductEntity>(view) {

    private val textName = view.findViewById<TextView>(R.id.tvItemName)
    private val viewDisable = view.findViewById<View>(R.id.vItemDisable)
    private val switchStopped = view.findViewById<SwitchCompat>(R.id.scItemSwitch)

    private var initMode = false

    override fun bind(
        item: ProductEntity,
        position: Int,
        glide: GlideRequests?,
        viewModel: StopsViewModel,
        isLast: Boolean,
        click: (ProductEntity, Any?) -> Unit
    ) {
        textName.text = item.title
        viewDisable.setOnClickListener {
            click(item, true)
        }
        switchStopped.setOnCheckedChangeListener { _, _ ->
            if (initMode.not()) {
                click(item, false)
                item.isStopped = false
                setChangeableData(item)
            }
        }
        setChangeableData(item)
        viewModel.products.observe(this, { products ->
            if (products is ResultEntity.Success) {
                val itemInList = products.data.firstOrNull { it.id == item.id }
                itemInList?.let {
                    setChangeableData(it)
                }
            }
        })
    }

    private fun setChangeableData(item: ProductEntity) {
        initMode = true
        if (item.isStopped) {
            viewDisable.visibility = View.GONE
            switchStopped.isEnabled = true
            switchStopped.isChecked = true
        } else {
            viewDisable.visibility = View.VISIBLE
            switchStopped.isEnabled = false
            switchStopped.isChecked = false
        }
        initMode = false
    }

    companion object {
        fun from(parent: ViewGroup): ProductHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_stop, parent, false)
            return ProductHolder(view)
        }
    }
}