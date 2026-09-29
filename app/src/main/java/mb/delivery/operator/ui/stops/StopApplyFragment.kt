package mb.delivery.operator.ui.stops

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import mb.delivery.operator.R
import mb.delivery.operator.databinding.FragmentStopApplyBinding
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.ui.adapters.StopPickAdapter
import mb.delivery.operator.ui.base.BaseDialogFragment
import mb.delivery.operator.ui.dialogs.OrganizationPickDialog

class StopApplyFragment : BaseDialogFragment() {

    private val viewModel: StopApplyViewModel by viewModel()

    private var _binding: FragmentStopApplyBinding? = null
    private val binding get() = _binding

    private var orgs: List<OrganizationKitchenEntity> = listOf()
    private var pickAdapter: StopPickAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(OrganizationPickDialog.REQUEST_KEY) { _, bundle ->
            val id = bundle.getInt(OrganizationPickDialog.RESULT_ORG_ID, 0)
            orgs.firstOrNull { it.id == id }?.let { viewModel.selectOrganization(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStopApplyBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.tvStops?.isChecked = true
        initViews()
        initListeners()
        initObservers()
        viewModel.load()
    }

    private fun initViews() {
        if (binding?.rvProducts?.itemDecorationCount == 0) {
            binding?.rvProducts?.addItemDecoration(
                DividerItemDecoration(
                    requireContext(), LinearLayoutManager.VERTICAL
                ).also {
                    ContextCompat.getDrawable(requireContext(), R.drawable.divider)?.let { d ->
                        it.setDrawable(d)
                    }
                }
            )
        }
        pickAdapter = StopPickAdapter(
            onCategory = { viewModel.openCategory(it) },
            onProduct = { viewModel.apply(it) }
        )
        binding?.rvProducts?.adapter = pickAdapter
    }

    private fun initListeners() {
        binding?.ivClose?.setOnClickListener { close() }
        binding?.tvOrganization?.setOnClickListener { showOrgPicker() }
        binding?.tvBackCategories?.setOnClickListener { viewModel.backToCategories() }
        binding?.etSearch?.doAfterTextChanged {
            viewModel.search.postValue(it?.toString().orEmpty())
        }
    }

    private fun initObservers() {
        viewModel.organizations.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultEntity.Error -> {
                    Toast.makeText(
                        requireContext(),
                        result.error.message.ifEmpty { getString(R.string.stops_no_orgs) },
                        Toast.LENGTH_SHORT
                    ).show()
                    close()
                }
                is ResultEntity.Success -> bindOrgs(result.data.items)
                else -> Unit
            }
        }
        viewModel.selectedOrg.observe(viewLifecycleOwner) { org ->
            bindSelectedOrg(org)
            renderList()
        }
        viewModel.catalog.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultEntity.Error -> {
                    val message = result.error.message
                    if (message.isNotEmpty()) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
                else -> Unit
            }
            renderList()
        }
        viewModel.browseRows.observe(viewLifecycleOwner) {
            renderList()
        }
        viewModel.search.observe(viewLifecycleOwner) {
            renderList()
        }
        viewModel.selectedCategoryId.observe(viewLifecycleOwner) {
            renderList()
        }
        viewModel.applyRequest.observe(viewLifecycleOwner) { result ->
            if (result is ResultEntity.Error) {
                val message = result.error.message.ifEmpty { getString(R.string.stops_apply_error) }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
            if (result is ResultEntity.Success) {
                setFragmentResult(
                    StopsFragment.REQUEST_STOP_CHANGED,
                    bundleOf()
                )
                findNavController().previousBackStackEntry?.savedStateHandle
                    ?.set(StopsFragment.REQUEST_STOP_CHANGED, true)
            }
        }
        viewModel.close.observe(viewLifecycleOwner) { shouldClose ->
            if (shouldClose == true) {
                close()
            }
        }
    }

    private fun bindOrgs(items: List<OrganizationKitchenEntity>) {
        if (items.isEmpty()) {
            Toast.makeText(requireContext(), R.string.stops_no_orgs, Toast.LENGTH_SHORT).show()
            close()
            return
        }
        orgs = items
        val many = items.size > 1
        binding?.tvOrgTitle?.isVisible = true
        binding?.tvOrganization?.isVisible = true
        binding?.tvOrganization?.isClickable = many
        binding?.tvOrganization?.isEnabled = many
        bindSelectedOrg(viewModel.selectedOrg.value)
    }

    private fun showOrgPicker() {
        if (orgs.size <= 1) {
            return
        }
        OrganizationPickDialog.show(
            parentFragmentManager,
            orgs,
            viewModel.selectedOrg.value?.id
        )
    }

    private fun bindSelectedOrg(org: OrganizationKitchenEntity?) {
        binding?.tvOrganization?.text = org?.label().orEmpty()
    }

    private fun renderList() {
        val rows = viewModel.browseRows.value.orEmpty()
        pickAdapter?.submitList(rows)
        val loading = viewModel.catalog.value is ResultEntity.Loading
        val hasOrg = viewModel.selectedOrg.value != null
        val searching = !viewModel.search.value.isNullOrBlank()
        val insideCategory = viewModel.isInsideCategory() && !searching

        binding?.tvBackCategories?.isVisible = insideCategory
        binding?.tvCategoryTitle?.isVisible = insideCategory
        binding?.tvCategoryTitle?.text = viewModel.categoryTitle().orEmpty()

        binding?.tvEmpty?.isVisible = !loading && rows.isEmpty()
        binding?.tvEmpty?.setText(
            when {
                !hasOrg -> R.string.stops_pick_org
                searching || insideCategory -> R.string.stops_empty
                else -> R.string.stops_categories_empty
            }
        )
        binding?.rvProducts?.isVisible = rows.isNotEmpty()
    }

    private fun close() {
        findNavController().navigateUp()
    }
}
