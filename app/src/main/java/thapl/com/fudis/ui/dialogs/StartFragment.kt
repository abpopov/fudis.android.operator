package thapl.com.fudis.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import thapl.com.fudis.databinding.FragmentStartBinding
import thapl.com.fudis.ui.base.BaseDialogFragment
import thapl.com.fudis.ui.orders.OrdersFragment

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
        viewModel.drop()
        initViews()
        initListeners()
    }

    private fun initViews() {
        binding?.tvPause?.isChecked = true
    }

    private fun initListeners() {
        binding?.ivClose?.setOnClickListener {
            setFragmentResult(OrdersFragment.SOURCE_MENU, bundleOf(OrdersFragment.SOURCE_MENU to args.source))
            navigate(StartFragmentDirections.actionClose())
        }
        binding?.tvActionPause?.setOnClickListener {
            viewModel.start()
            setFragmentResult(OrdersFragment.SOURCE_MENU, bundleOf(OrdersFragment.SOURCE_MENU to args.source))
            navigate(StartFragmentDirections.actionClose())
        }
    }
}