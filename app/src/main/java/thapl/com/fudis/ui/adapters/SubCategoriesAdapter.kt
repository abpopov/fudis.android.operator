package thapl.com.fudis.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import thapl.com.fudis.GlideRequests
import thapl.com.fudis.R
import thapl.com.fudis.domain.model.CategoryEntity
import thapl.com.fudis.ui.base.BaseHolder
import thapl.com.fudis.ui.base.BaseListAdapter
import thapl.com.fudis.ui.categories.CategoriesViewModel

class SubCategoriesAdapter(
    glide: GlideRequests?,
    viewModel: CategoriesViewModel,
    click: (CategoryEntity, Any?) -> Unit
) :
    BaseListAdapter<CategoriesViewModel, CategoryEntity, SubCategoryHolder>(
        glide,
        viewModel,
        click,
        getHolder = { parent, _ ->
            SubCategoryHolder.from(parent)
        }
    )

class SubCategoryHolder(view: View) : BaseHolder<CategoriesViewModel, CategoryEntity>(view) {

    private val textName = view.findViewById<TextView>(R.id.tvItemName)
    private val textCounter = view.findViewById<TextView>(R.id.tvItemCount)
    private val listCatalog = view.findViewById<RecyclerView>(R.id.rvCatalog)
    private val viewMotion = view.findViewById<MotionLayout>(R.id.vRoot)

    override fun bind(
        item: CategoryEntity,
        position: Int,
        glide: GlideRequests?,
        viewModel: CategoriesViewModel,
        isLast: Boolean,
        click: (CategoryEntity, Any?) -> Unit
    ) {
        textName.text = item.title
        textCounter.text = item.counter
        textName.setOnClickListener {
            if (viewMotion.progress < 0.5f) {
                viewMotion.transitionToEnd()
            } else {
                viewMotion.transitionToStart()
            }
        }
        val catalogAdapter = CatalogAdapter(glide, viewModel, click = { child, _ ->
            click(item, child)
        })
        listCatalog.adapter = catalogAdapter
        catalogAdapter.submitList(item.children.mapIndexed { index, catalogEntity ->
            catalogEntity.also {
                it.counter = "${position + 1}.${index + 1}"
            }
        })
    }

    companion object {
        fun from(parent: ViewGroup): SubCategoryHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_sub, parent, false)
            return SubCategoryHolder(view)
        }
    }
}