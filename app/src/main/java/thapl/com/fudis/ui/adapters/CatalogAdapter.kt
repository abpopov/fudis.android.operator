package thapl.com.fudis.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import thapl.com.fudis.GlideRequests
import thapl.com.fudis.R
import thapl.com.fudis.domain.model.CatalogEntity
import thapl.com.fudis.ui.base.BaseHolder
import thapl.com.fudis.ui.base.BaseListAdapter
import thapl.com.fudis.ui.categories.CategoriesViewModel

class CatalogAdapter(
    glide: GlideRequests?,
    viewModel: CategoriesViewModel,
    click: (CatalogEntity, Any?) -> Unit
) :
    BaseListAdapter<CategoriesViewModel, CatalogEntity, CatalogHolder>(
        glide,
        viewModel,
        click,
        getHolder = { parent, _ ->
            CatalogHolder.from(parent)
        }
    )

class CatalogHolder(view: View) : BaseHolder<CategoriesViewModel, CatalogEntity>(view) {

    private val textName = view.findViewById<TextView>(R.id.tvItemName)
    private val textCounter = view.findViewById<TextView>(R.id.tvItemCount)
    private val viewSpace = view.findViewById<View>(R.id.vSpace2)

    override fun bind(
        item: CatalogEntity,
        position: Int,
        glide: GlideRequests?,
        viewModel: CategoriesViewModel,
        isLast: Boolean,
        click: (CatalogEntity, Any?) -> Unit
    ) {
        textName.text = item.title
        textCounter.text = item.counter
        viewSpace.visibility = if (item.isRoot) View.GONE else View.VISIBLE
        itemView.setOnClickListener {
            click(item, null)
        }
    }

    companion object {
        fun from(parent: ViewGroup): CatalogHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_catalog, parent, false)
            return CatalogHolder(view)
        }
    }
}