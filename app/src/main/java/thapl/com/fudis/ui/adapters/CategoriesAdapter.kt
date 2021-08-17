package thapl.com.fudis.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
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
    private val listSubCategories = view.findViewById<RecyclerView>(R.id.rvSubs)
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
        textName.setOnClickListener {
            if (viewMotion.progress < 0.5f) {
                viewMotion.transitionToEnd()
            } else {
                viewMotion.transitionToStart()
            }
        }
        fun initAdapter() {
            val subCategoryAdapter = SubCategoriesAdapter(glide, viewModel, click = { _, deepChild ->
                click(item, deepChild)
            })
            val catalogAdapter = CatalogAdapter(glide, viewModel, click = { child, _ ->
                click(item, child)
            })
            listSubCategories.adapter = ConcatAdapter(subCategoryAdapter, catalogAdapter)
            subCategoryAdapter.submitList(item.subCategories.mapIndexedNotNull { index, categoryEntity ->
                if (categoryEntity.children.isEmpty()) null else categoryEntity.also {
                    it.counter = "${index + 1}"
                }
            })
            catalogAdapter.submitList(item.children.mapIndexed { index, catalogEntity ->
                catalogEntity.also {
                    it.counter = (item.subCategories.mapNotNull { categoryEntity ->
                        if (categoryEntity.children.isEmpty()) null else categoryEntity
                    }.size + index + 1).toString()
                    it.isRoot = true
                }
            })
        }
        viewMotion.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {

            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {

            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (currentId == R.id.start) {
                    initAdapter()
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {

            }

        })
        initAdapter()
    }

    companion object {
        fun from(parent: ViewGroup): CategoryHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_category, parent, false)
            return CategoryHolder(view)
        }
    }
}