package mb.delivery.operator.ui.highload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import mb.delivery.operator.R
import mb.delivery.operator.databinding.FragmentHighloadBinding
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.ui.adapters.HighloadAdapter
import mb.delivery.operator.ui.base.BaseFragment
import mb.delivery.operator.ui.orders.OrdersFragmentDirections

class HighloadFragment : BaseFragment() {

    private val viewModel: HighloadViewModel by viewModel()

    private var _binding: FragmentHighloadBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHighloadBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        if (binding?.rvHighload?.itemDecorationCount == 0) {
            binding?.rvHighload?.addItemDecoration(
                DividerItemDecoration(
                    requireContext(), LinearLayoutManager.VERTICAL
                ).also {
                    ContextCompat.getDrawable(requireContext(), R.drawable.divider)?.let { d ->
                        it.setDrawable(d)
                    }
                }
            )
        }
        binding?.rvHighload?.adapter = HighloadAdapter(viewModel) { item, _ ->
            viewModel.drop(item.id)
        }
    }

    private fun initListeners() {
        binding?.tvReload?.setOnClickListener { viewModel.load() }
        binding?.tvApply?.setOnClickListener {
            if (viewModel.hasIdleOrgs()) {
                openApplyDialog()
            } else {
                Toast.makeText(requireContext(), R.string.highload_no_idle, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initObservers() {
        viewModel.catalog.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultEntity.Loading -> {
                    binding?.tvReload?.isVisible = false
                    binding?.rvHighload?.isVisible = true
                    binding?.tvEmpty?.isVisible = false
                    binding?.tvApply?.isEnabled = false
                }
                is ResultEntity.Error -> {
                    binding?.tvReload?.isVisible = true
                    binding?.rvHighload?.isVisible = false
                    binding?.tvEmpty?.isVisible = false
                    binding?.tvApply?.isEnabled = false
                    val message = result.error.message
                    if (message.isNotEmpty()) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
                is ResultEntity.Success -> {
                    binding?.tvReload?.isVisible = false
                    val active = viewModel.activeOrgs()
                    (binding?.rvHighload?.adapter as? HighloadAdapter)?.submitList(active)
                    binding?.rvHighload?.isVisible = active.isNotEmpty()
                    binding?.tvEmpty?.isVisible = active.isEmpty()
                    binding?.tvApply?.isEnabled = viewModel.hasIdleOrgs()
                }
            }
        }
        viewModel.dropRequest.observe(viewLifecycleOwner) { result ->
            if (result is ResultEntity.Error) {
                val message = result.error.message.ifEmpty { getString(R.string.highload_drop_error) }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openApplyDialog() {
        try {
            Navigation.findNavController(requireActivity(), R.id.nav_fragment)
                .navigate(OrdersFragmentDirections.actionPause(1))
        } catch (e: Exception) {
            Toast.makeText(requireContext(), R.string.pause_load_error, Toast.LENGTH_SHORT).show()
        }
    }
}
