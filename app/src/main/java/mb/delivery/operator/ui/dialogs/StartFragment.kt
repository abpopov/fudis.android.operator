package mb.delivery.operator.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import mb.delivery.operator.databinding.FragmentStartBinding
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.ui.base.BaseDialogFragment
import mb.delivery.operator.ui.orders.OrdersFragment

class StartFragment : BaseDialogFragment() {

    private val viewModel: PauseViewModel by sharedViewModel()

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding

    private val args: PauseFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.tvPause?.isChecked = true
        initListeners()
        initObservers()
        viewModel.load()
    }

    private fun initListeners() {
        binding?.ivClose?.setOnClickListener { close() }
        binding?.tvActionPause?.setOnClickListener { viewModel.submit() }
    }

    private fun initObservers() {
        viewModel.canSubmit.observe(viewLifecycleOwner) { enabled ->
            binding?.tvActionPause?.isEnabled = enabled == true
        }
        viewModel.request.observe(viewLifecycleOwner) { result ->
            if (result is ResultEntity.Error) {
                binding?.tvActionPause?.isEnabled = viewModel.canSubmit.value == true
                val message = result.error.message
                if (message.isNotEmpty()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.close.observe(viewLifecycleOwner) { shouldClose ->
            if (shouldClose == true) {
                close()
            }
        }
    }

    private fun close() {
        setFragmentResult(OrdersFragment.SOURCE_MENU, bundleOf(OrdersFragment.SOURCE_MENU to args.source))
        navigate(StartFragmentDirections.actionClose())
    }
}
