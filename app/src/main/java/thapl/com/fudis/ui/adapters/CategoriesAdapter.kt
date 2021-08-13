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
import thapl.com.fudis.ui.categories.CategoriesViewModel

class CategoriesAdapter(
    glide: GlideRequests?,
    viewModel: CategoriesViewModel,
    click: (CategoryEntity, Any?) -> Unit
) :
    BaseListAdapter<CategoriesViewModel, CategoryEntity, CategoryHolder>(
        glide,
        viewModel,
        click,
        getHolder = { parent, _ ->
            CategoryHolder.from(parent)
        }
    )

class CategoryHolder(view: View) : BaseHolder<CategoriesViewModel, CategoryEntity>(view) {

    private val textName = view.findViewById<TextView>(R.id.tvItemName)

    override fun bind(
        item: CategoryEntity,
        glide: GlideRequests?,
        viewModel: CategoriesViewModel,
        isLast: Boolean,
        click: (CategoryEntity, Any?) -> Unit
    ) {
        textName.text = item.title
        setChangeableData(item)
        viewModel.categories.observe(this, { cats ->
            if (cats is ResultEntity.Success) {
                val itemInList = cats.data.firstOrNull { it.id == item.id }
                itemInList?.let {
                    setChangeableData(it)
                }
            }
        })
    }

    private fun setChangeableData(item: CategoryEntity) {

    }

    companion object {
        fun from(parent: ViewGroup): CategoryHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_category, parent, false)
            return CategoryHolder(view)
        }
    }
}