package mb.delivery.operator.ui.dialogs

import android.os.Bundle
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
import mb.delivery.operator.domain.model.OrganizationKitchenEntity

class OrganizationPickDialog : PanelDialogFragment() {

    override fun bodyLayoutRes() = R.layout.include_dialog_list
    override fun titleRes() = R.string.order_edit_organization_title

    private var selectedId: Int = 0

    override fun bindBody(root: View) {
        selectedId = arguments?.getInt(ARG_SELECTED_ID, 0) ?: 0
        val titles = arguments?.getStringArrayList(ARG_TITLES).orEmpty()
        val ids = arguments?.getIntegerArrayList(ARG_IDS).orEmpty()
        val rv = root.findViewById<RecyclerView>(R.id.rvItems)
        rv.adapter = ChoiceAdapter(titles, ids, selectedId) { id ->
            selectedId = id
            rv.adapter?.notifyDataSetChanged()
            setFragmentResult(REQUEST_KEY, bundleOf(RESULT_ORG_ID to id))
            dismissAllowingStateLoss()
        }
        btnPrimary?.isVisible = false
        setSecondaryVisible(true, R.string.order_edit_cancel)
    }

    private class ChoiceAdapter(
        private val titles: List<String>,
        private val ids: List<Int>,
        private var selectedId: Int,
        private val onPick: (Int) -> Unit
    ) : RecyclerView.Adapter<ChoiceAdapter.Holder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_dialog_choice, parent, false)
            return Holder(view)
        }

        override fun getItemCount() = titles.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val id = ids.getOrNull(position) ?: return
            holder.title.text = titles[position]
            holder.check.isVisible = id == selectedId
            holder.itemView.setOnClickListener {
                selectedId = id
                onPick(id)
            }
        }

        class Holder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.tvTitle)
            val check: ImageView = view.findViewById(R.id.ivCheck)
        }
    }

    companion object {
        const val TAG = "OrganizationPickDialog"
        const val REQUEST_KEY = "organization_pick_result"
        const val RESULT_ORG_ID = "organization_id"
        private const val ARG_TITLES = "titles"
        private const val ARG_IDS = "ids"
        private const val ARG_SELECTED_ID = "selected_id"

        fun show(
            fm: FragmentManager,
            organizations: List<OrganizationKitchenEntity>,
            selectedId: Int?
        ) {
            OrganizationPickDialog().apply {
                arguments = bundleOf(
                    ARG_TITLES to ArrayList(organizations.map { it.label() }),
                    ARG_IDS to ArrayList(organizations.map { it.id }),
                    ARG_SELECTED_ID to (selectedId ?: 0)
                )
            }.show(fm, TAG)
        }
    }
}
