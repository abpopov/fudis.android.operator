package thapl.com.fudis.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import thapl.com.fudis.databinding.FragmentPauseBinding
import thapl.com.fudis.ui.base.BaseDialogFragment
import thapl.com.fudis.ui.orders.OrdersFragment

class PauseFragment : BaseDialogFragment() {

    private val viewModel: PauseViewModel by sharedViewModel()

    private var _binding: FragmentPauseBinding? = null
    private val binding get() = _binding

    private val args: PauseFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPauseBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.drop()
        initViews()
        initListeners()
        initObservers()
    }

    private fun initViews() {
        binding?.tvPause?.isChecked = true
        binding?.rgPeriod?.setOnCheckedChangeListener { radioGroup, i ->
            viewModel.selectState(i, true)
        }
        binding?.rgReason?.setOnCheckedChangeListener { radioGroup, i ->
            viewModel.selectState(i, false)
        }
    }

    private fun initListeners() {
        binding?.ivClose?.setOnClickListener {
            setFragmentResult(OrdersFragment.SOURCE_MENU, bundleOf(OrdersFragment.SOURCE_MENU to args.source))
            navigate(HelpFragmentDirections.actionClose())
        }
        binding?.tvActionPause?.setOnClickListener {

        }
    }

    private fun initObservers() {
        viewModel.pauseState.observe(this, { state ->
            binding?.tvActionPause?.isEnabled = state?.first != null && state.second != null
        })
    }
}