package mb.delivery.operator.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import mb.delivery.operator.R
import mb.delivery.operator.data.api.model.OrderCartItemRequestApi
import mb.delivery.operator.ui.orders.OrdersViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Редактирование состава: ± / удалить, «Добавить» и «Сохранить».
 * Черновик — [OrdersViewModel.cartEditDraft] (модификаторы сохраняются).
 */
class CartEditDialog : PanelDialogFragment() {

    private val viewModel: OrdersViewModel by sharedViewModel()

    override fun bodyLayoutRes() = R.layout.include_dialog_cart
    override fun titleRes() = R.string.order_edit_cart_title

    private var adapter: CartEditAdapter? = null
    private var tvEmpty: TextView? = null

    override fun bindBody(root: View) {
        tvEmpty = root.findViewById(R.id.tvEmpty)
        val rv = root.findViewById<RecyclerView>(R.id.rvItems)
        adapter = CartEditAdapter(
            draft = viewModel.cartEditDraft,
            titles = viewModel.cartEditTitles,
            onMinus = { index ->
                val item = viewModel.cartEditDraft.getOrNull(index) ?: return@CartEditAdapter
                if (item.count <= 1) {
                    viewModel.cartEditDraft.removeAt(index)
                    if (index < viewModel.cartEditTitles.size) {
                        viewModel.cartEditTitles.removeAt(index)
                    }
                } else {
                    viewModel.cartEditDraft[index] = item.copy(count = item.count - 1)
                }
                refreshList()
            },
            onPlus = { index ->
                val item = viewModel.cartEditDraft.getOrNull(index) ?: return@CartEditAdapter
                viewModel.cartEditDraft[index] = item.copy(count = item.count + 1)
                refreshList()
            },
            onRemove = { index ->
                if (index in viewModel.cartEditDraft.indices) {
                    viewModel.cartEditDraft.removeAt(index)
                    if (index < viewModel.cartEditTitles.size) {
                        viewModel.cartEditTitles.removeAt(index)
                    }
                    refreshList()
                }
            }
        )
        rv.adapter = adapter
        setPrimaryText(R.string.order_edit_cart_save)
        setSecondaryVisible(true, R.string.order_edit_cart_add)
        refreshList()
    }

    override fun onPrimaryClick() {
        if (viewModel.cartEditDraft.isEmpty()) {
            Toast.makeText(requireContext(), R.string.order_edit_cart_empty, Toast.LENGTH_SHORT).show()
            return
        }
        setFragmentResult(REQUEST_KEY, bundleOf(RESULT_ACTION to ACTION_SAVE))
        dismissAllowingStateLoss()
    }

    override fun onSecondaryClick() {
        setFragmentResult(REQUEST_KEY, bundleOf(RESULT_ACTION to ACTION_ADD))
        dismissAllowingStateLoss()
    }

    private fun refreshList() {
        adapter?.notifyDataSetChanged()
        tvEmpty?.isVisible = viewModel.cartEditDraft.isEmpty()
    }

    private class CartEditAdapter(
        private val draft: List<OrderCartItemRequestApi>,
        private val titles: List<String>,
        private val onMinus: (Int) -> Unit,
        private val onPlus: (Int) -> Unit,
        private val onRemove: (Int) -> Unit
    ) : RecyclerView.Adapter<CartEditAdapter.Holder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_cart_edit, parent, false)
            return Holder(view)
        }

        override fun getItemCount() = draft.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val item = draft[position]
            val title = titles.getOrNull(position) ?: "#${item.catalogItemId}"
            holder.title.text = "${item.count} × $title"
            holder.count.text = item.count.toString()
            holder.btnMinus.setOnClickListener {
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onMinus(pos)
            }
            holder.btnPlus.setOnClickListener {
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onPlus(pos)
            }
            holder.btnRemove.setOnClickListener {
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onRemove(pos)
            }
        }

        class Holder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.tvTitle)
            val count: TextView = view.findViewById(R.id.tvCount)
            val btnMinus: TextView = view.findViewById(R.id.btnMinus)
            val btnPlus: TextView = view.findViewById(R.id.btnPlus)
            val btnRemove: TextView = view.findViewById(R.id.btnRemove)
        }
    }

    companion object {
        const val TAG = "CartEditDialog"
        const val REQUEST_KEY = "cart_edit_result"
        const val RESULT_ACTION = "action"
        const val ACTION_SAVE = "save"
        const val ACTION_ADD = "add"

        fun show(fm: FragmentManager) {
            CartEditDialog().show(fm, TAG)
        }
    }
}
