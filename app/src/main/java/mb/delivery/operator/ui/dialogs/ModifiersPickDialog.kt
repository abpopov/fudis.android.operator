package mb.delivery.operator.ui.dialogs

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import mb.delivery.operator.R
import mb.delivery.operator.data.api.model.EditModificatorApi
import java.text.NumberFormat

class ModifiersPickDialog : PanelDialogFragment() {

    override fun bodyLayoutRes() = R.layout.include_dialog_list
    override fun titleRes() = R.string.order_edit_modifiers_title

    private val selected = mutableSetOf<Int>()
    private var adapter: MultiChoiceAdapter? = null
    private var resultSent = false

    override fun bindBody(root: View) {
        val titles = arguments?.getStringArrayList(ARG_TITLES).orEmpty()
        val rv = root.findViewById<RecyclerView>(R.id.rvItems)
        adapter = MultiChoiceAdapter(titles, selected) { index ->
            if (selected.contains(index)) selected.remove(index) else selected.add(index)
            adapter?.notifyItemChanged(index)
        }
        rv.adapter = adapter
        setPrimaryText(R.string.order_edit_save)
        setSecondaryVisible(true, R.string.order_edit_cancel)
    }

    override fun onPrimaryClick() {
        val modIds = arguments?.getSerializable(ARG_MOD_IDS) as? ArrayList<*> ?: arrayListOf<Any>()
        val picked = selected.mapNotNull { index ->
            (modIds.getOrNull(index) as? Number)?.toLong()
        }
        resultSent = true
        setFragmentResult(
            REQUEST_KEY,
            bundleOf(
                RESULT_PRODUCT_ID to (arguments?.getLong(ARG_PRODUCT_ID) ?: 0L),
                RESULT_MOD_IDS to picked.toLongArray()
            )
        )
        dismissAllowingStateLoss()
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

    private class MultiChoiceAdapter(
        private val titles: List<String>,
        private val selected: Set<Int>,
        private val onToggle: (Int) -> Unit
    ) : RecyclerView.Adapter<MultiChoiceAdapter.Holder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_dialog_choice, parent, false)
            return Holder(view)
        }

        override fun getItemCount() = titles.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.title.text = titles[position]
            holder.check.isVisible = selected.contains(position)
            holder.itemView.setOnClickListener { onToggle(position) }
        }

        class Holder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.tvTitle)
            val check: ImageView = view.findViewById(R.id.ivCheck)
        }
    }

    companion object {
        const val TAG = "ModifiersPickDialog"
        const val REQUEST_KEY = "modifiers_pick_result"
        const val RESULT_PRODUCT_ID = "product_id"
        const val RESULT_MOD_IDS = "mod_ids"
        const val RESULT_CANCELLED = "cancelled"
        private const val ARG_PRODUCT_ID = "product_id"
        private const val ARG_TITLES = "titles"
        private const val ARG_MOD_IDS = "mod_ids"

        fun show(
            fm: FragmentManager,
            productId: Long,
            modifiers: List<EditModificatorApi>,
            formatter: NumberFormat
        ) {
            val withId = modifiers.filter { it.id != null }
            ModifiersPickDialog().apply {
                arguments = bundleOf(
                    ARG_PRODUCT_ID to productId,
                    ARG_TITLES to ArrayList(withId.map {
                        val price = it.price ?: 0f
                        "${it.title.orEmpty()} (+${formatter.format(price)} ₽)"
                    }),
                    ARG_MOD_IDS to ArrayList(withId.map { it.id!! })
                )
            }.show(fm, TAG)
        }
    }
}
