package mb.delivery.operator.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mb.delivery.operator.R
import mb.delivery.operator.domain.model.ProductEntity
import mb.delivery.operator.domain.model.StopProductCategoryEntity
import mb.delivery.operator.ui.stops.StopPickRow

class StopPickAdapter(
    private val onCategory: (StopProductCategoryEntity) -> Unit,
    private val onProduct: (ProductEntity) -> Unit
) : ListAdapter<StopPickRow, RecyclerView.ViewHolder>(Diff) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is StopPickRow.Category -> TYPE_CATEGORY
        is StopPickRow.Product -> TYPE_PRODUCT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_CATEGORY -> CategoryHolder(
                inflater.inflate(R.layout.list_item_stop_category, parent, false)
            )
            else -> ProductHolder(
                inflater.inflate(R.layout.list_item_stop_product, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is StopPickRow.Category -> (holder as CategoryHolder).bind(item.category, onCategory)
            is StopPickRow.Product -> (holder as ProductHolder).bind(item.product, onProduct)
        }
    }

    private class CategoryHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.tvItemName)
        private val count: TextView = view.findViewById(R.id.tvCount)

        fun bind(category: StopProductCategoryEntity, onCategory: (StopProductCategoryEntity) -> Unit) {
            title.text = category.title
            count.text = category.availableCount.toString()
            itemView.setOnClickListener { onCategory(category) }
        }
    }

    private class ProductHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.tvItemName)

        fun bind(product: ProductEntity, onProduct: (ProductEntity) -> Unit) {
            title.text = product.title
            itemView.setOnClickListener { onProduct(product) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<StopPickRow>() {
        override fun areItemsTheSame(oldItem: StopPickRow, newItem: StopPickRow): Boolean {
            return when {
                oldItem is StopPickRow.Category && newItem is StopPickRow.Category ->
                    oldItem.category.id == newItem.category.id
                oldItem is StopPickRow.Product && newItem is StopPickRow.Product ->
                    oldItem.product.id == newItem.product.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: StopPickRow, newItem: StopPickRow): Boolean =
            oldItem == newItem
    }

    companion object {
        private const val TYPE_CATEGORY = 1
        private const val TYPE_PRODUCT = 2
    }
}
