package thapl.com.fudis.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import thapl.com.fudis.databinding.FragmentOrdersBinding
import thapl.com.fudis.ui.base.BaseFragment

class OrdersFragment : BaseFragment() {

    private val viewModel: OrdersViewModel by sharedViewModel()

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {

    }

    private fun initListeners() {
        binding?.tvOrders?.setOnClickListener {
            viewModel.selectMenu(0)
        }
        binding?.tvPause?.setOnClickListener {
            viewModel.selectMenu(1)
        }
        binding?.tvStops?.setOnClickListener {
            navigate(OrdersFragmentDirections.actionStops())
        }
        binding?.tvHelp?.setOnClickListener {
            viewModel.selectMenu(3)
        }
    }

    private fun initObservers() {
        viewModel.menuPos.observe(viewLifecycleOwner, { pos ->
            binding?.tvOrders?.isChecked = pos == 0
            binding?.tvPause?.isChecked = pos == 1
            binding?.tvStops?.isChecked = pos == 2
            binding?.tvHelp?.isChecked = pos == 3
            binding?.tvOrders?.isEnabled = pos != 0
            binding?.tvPause?.isEnabled = pos != 1
            binding?.tvStops?.isEnabled = pos != 2
            binding?.tvHelp?.isEnabled = pos != 3
        })
    }
}