package mb.delivery.operator.ui.dialogs

import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import mb.delivery.operator.R
import mb.delivery.operator.data.api.model.EditProductApi
import mb.delivery.operator.data.api.model.EditProductCategoryApi
import mb.delivery.operator.ui.orders.OrdersViewModel
import mb.delivery.operator.utils.containsWord
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Выбор блюда для состава заказа: категории → позиции, плюс поиск.
 */
class ProductPickDialog : PanelDialogFragment() {

    private val viewModel: OrdersViewModel by sharedViewModel()

    override fun bodyLayoutRes() = R.layout.include_dialog_product_pick
    override fun titleRes() = R.string.order_edit_cart_add

    private var resultSent = false
    private var selectedCategoryId: Long? = null
    private var searchQuery: String = ""

    private var etSearch: EditText? = null
    private var vCategoryHeader: View? = null
    private var tvBack: TextView? = null
    private var tvCategoryTitle: TextView? = null
    private var tvEmpty: TextView? = null
    private var adapter: PickAdapter? = null

    override fun bindBody(root: View) {
        etSearch = root.findViewById(R.id.etSearch)
        vCategoryHeader = root.findViewById(R.id.vCategoryHeader)
        tvBack = root.findViewById(R.id.tvBackCategories)
        tvCategoryTitle = root.findViewById(R.id.tvCategoryTitle)
        tvEmpty = root.findViewById(R.id.tvEmpty)
        val rv = root.findViewById<RecyclerView>(R.id.rvItems)

        adapter = PickAdapter(
            onCategory = { catId, title ->
                selectedCategoryId = catId
                searchQuery = ""
                etSearch?.setText("")
                refreshList()
                tvCategoryTitle?.text = title
            },
            onProduct = { productId ->
                resultSent = true
                setFragmentResult(REQUEST_KEY, bundleOf(RESULT_PRODUCT_ID to productId))
                dismissAllowingStateLoss()
            }
        )
        rv.adapter = adapter

        tvBack?.setOnClickListener {
            selectedCategoryId = null
            refreshList()
        }
        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString().orEmpty()
                refreshList()
            }
        })

        btnPrimary?.isVisible = false
        setSecondaryVisible(true, R.string.order_edit_cancel)
        refreshList()
    }

    override fun onSecondaryClick() {
        resultSent = true
        setFragmentResult(REQUEST_KEY, bundleOf(RESULT_CANCELLED to true))
        dismissAllowingStateLoss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (!resultSent) {
            setFragmentResult(REQUEST_KEY, bundleOf(RESULT_CANCELLED to true))
        }
        super.onDismiss(dialog)
    }

    private fun refreshList() {
        val catalog = viewModel.editProductsCatalog
        val categories = catalog?.categories.orEmpty()
        val items = catalog?.items.orEmpty()
        val query = searchQuery.trim()
        val rows = when {
            query.isNotEmpty() -> {
                items.filter {
                    it.id != null && (it.title.orEmpty().containsWord(query) ||
                        it.baseTitle.orEmpty().containsWord(query))
                }.map {
                    PickRow.Product(it.id!!, it.title.orEmpty().ifBlank { "#${it.id}" })
                }
            }
            selectedCategoryId != null -> {
                val cat = categories.firstOrNull { it.id == selectedCategoryId }
                cat?.items.orEmpty().mapNotNull { p ->
                    val id = p.id ?: return@mapNotNull null
                    PickRow.Product(id, p.title.orEmpty().ifBlank { "#$id" })
                }
            }
            else -> {
                categories.mapNotNull { cat ->
                    val id = cat.id ?: return@mapNotNull null
                    val title = cat.title.orEmpty().ifBlank { "#$id" }
                    val count = cat.items.orEmpty().count { it.id != null }
                    if (count <= 0) return@mapNotNull null
                    PickRow.Category(id, title, count)
                }
            }
        }
        adapter?.submit(rows)
        val inside = selectedCategoryId != null && query.isEmpty()
        vCategoryHeader?.isVisible = inside
        tvEmpty?.isVisible = rows.isEmpty()
        tvEmpty?.setText(
            when {
                query.isNotEmpty() || inside -> R.string.stops_empty
                else -> R.string.stops_categories_empty
            }
        )
    }

    private sealed class PickRow {
        data class Category(val id: Long, val title: String, val count: Int) : PickRow()
        data class Product(val id: Long, val title: String) : PickRow()
    }

    private class PickAdapter(
        private val onCategory: (Long, String) -> Unit,
        private val onProduct: (Long) -> Unit
    ) : RecyclerView.Adapter<PickAdapter.Holder>() {

        private val rows = mutableListOf<PickRow>()

        fun submit(list: List<PickRow>) {
            rows.clear()
            rows.addAll(list)
            notifyDataSetChanged()
        }

        override fun getItemViewType(position: Int): Int = when (rows[position]) {
            is PickRow.Category -> 1
            is PickRow.Product -> 2
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val layout = if (viewType == 1) {
                R.layout.list_item_stop_category
            } else {
                R.layout.list_item_dialog_choice
            }
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return Holder(view, viewType)
        }

        override fun getItemCount() = rows.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            when (val row = rows[position]) {
                is PickRow.Category -> {
                    holder.title?.text = row.title
                    holder.count?.text = row.count.toString()
                    holder.check?.isVisible = false
                    holder.itemView.setOnClickListener { onCategory(row.id, row.title) }
                }
                is PickRow.Product -> {
                    holder.title?.text = row.title
                    holder.count?.isVisible = false
                    holder.check?.isVisible = false
                    holder.itemView.setOnClickListener { onProduct(row.id) }
                }
            }
        }

        class Holder(view: View, viewType: Int) : RecyclerView.ViewHolder(view) {
            val title: TextView? = view.findViewById(
                if (viewType == 1) R.id.tvItemName else R.id.tvTitle
            )
            val count: TextView? = view.findViewById(R.id.tvCount)
            val check: ImageView? = view.findViewById(R.id.ivCheck)
        }
    }

    companion object {
        const val TAG = "ProductPickDialog"
        const val REQUEST_KEY = "product_pick_result"
        const val RESULT_PRODUCT_ID = "product_id"
        const val RESULT_CANCELLED = "cancelled"

        fun show(fm: FragmentManager) {
            ProductPickDialog().show(fm, TAG)
        }
    }
}
