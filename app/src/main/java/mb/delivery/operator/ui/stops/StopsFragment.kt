package mb.delivery.operator.ui.stops

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import mb.delivery.operator.R
import mb.delivery.operator.databinding.FragmentStopsBinding
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.ui.adapters.StopListAdapter
import mb.delivery.operator.ui.base.BaseFragment

class StopsFragment : BaseFragment() {

    private val viewModel: StopsViewModel by viewModel()

    private var _binding: FragmentStopsBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(REQUEST_STOP_CHANGED) { _, _ ->
            viewModel.loadStopList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStopsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        initObservers()
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Boolean>(REQUEST_STOP_CHANGED)
            ?.observe(viewLifecycleOwner) { changed ->
                if (changed == true) {
                    findNavController().currentBackStackEntry?.savedStateHandle
                        ?.set(REQUEST_STOP_CHANGED, false)
                    viewModel.loadStopList()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }

    override fun onStop() {
        viewModel.search.postValue("")
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        binding?.etSearch?.doAfterTextChanged {
            binding?.ivClear?.isVisible = !it.isNullOrEmpty()
            viewModel.search.postValue(it?.toString().orEmpty())
        }
        if (binding?.rvStopList?.itemDecorationCount == 0) {
            binding?.rvStopList?.addItemDecoration(
                DividerItemDecoration(
                    requireContext(), LinearLayoutManager.VERTICAL
                ).also {
                    ContextCompat.getDrawable(requireContext(), R.drawable.divider)?.let { d ->
                        it.setDrawable(d)
                    }
                }
            )
        }
        binding?.rvStopList?.adapter = StopListAdapter(viewModel) { item, _ ->
            viewModel.drop(item)
        }
    }

    private fun initListeners() {
        binding?.ivClear?.setOnClickListener {
            binding?.etSearch?.setText("")
        }
        binding?.tvReload?.setOnClickListener { viewModel.load() }
        binding?.tvOrganization?.setOnClickListener { showOrgPicker() }
        binding?.tvApply?.setOnClickListener {
            navigate(StopsFragmentDirections.actionApply())
        }
    }

    private fun initObservers() {
        viewModel.organizations.observe(viewLifecycleOwner) { result ->
            if (result is ResultEntity.Success) {
                bindOrgField(viewModel.selectedOrg.value)
            }
        }
        viewModel.selectedOrg.observe(viewLifecycleOwner) { org ->
            bindOrgField(org)
        }
        viewModel.items.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultEntity.Loading -> {
                    binding?.tvReload?.isVisible = false
                    binding?.tvEmpty?.isVisible = false
                    binding?.rvStopList?.isVisible = true
                }
                is ResultEntity.Error -> {
                    binding?.tvReload?.isVisible = true
                    binding?.tvEmpty?.isVisible = false
                    binding?.rvStopList?.isVisible = false
                    val message = result.error.message
                    if (message.isNotEmpty()) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
                is ResultEntity.Success -> {
                    binding?.tvReload?.isVisible = false
                }
            }
        }
        viewModel.visibleItems.observe(viewLifecycleOwner) { list ->
            val items = list.orEmpty()
            (binding?.rvStopList?.adapter as? StopListAdapter)?.submitList(items)
            val loading = viewModel.items.value is ResultEntity.Loading
            val error = viewModel.items.value is ResultEntity.Error
            binding?.rvStopList?.isVisible = !error && items.isNotEmpty()
            binding?.tvEmpty?.isVisible = !loading && !error && items.isEmpty()
            binding?.tvEmpty?.setText(
                if (viewModel.selectedOrg.value == null) R.string.stops_pick_org else R.string.stops_empty
            )
        }
        viewModel.dropRequest.observe(viewLifecycleOwner) { result ->
            if (result is ResultEntity.Error) {
                val message = result.error.message.ifEmpty { getString(R.string.stops_drop_error) }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindOrgField(org: OrganizationKitchenEntity?) {
        val many = viewModel.hasManyOrganizations()
        binding?.tvOrganization?.isVisible = true
        binding?.tvOrganization?.isClickable = many
        binding?.tvOrganization?.isEnabled = many
        binding?.tvOrganization?.text = org?.label().orEmpty()
    }

    private fun showOrgPicker() {
        val orgs = viewModel.organizationsList()
        if (orgs.size <= 1) {
            return
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.pause_org_title)
            .setItems(orgs.map { it.label() }.toTypedArray()) { _, which ->
                orgs.getOrNull(which)?.let { viewModel.selectOrganization(it) }
            }
            .show()
    }

    companion object {
        const val GOOD_ID = "good_id"
        const val REQUEST_STOP_CHANGED = "stop_changed"
    }
}
