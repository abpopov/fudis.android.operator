package thapl.com.fudis.ui.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import thapl.com.fudis.databinding.FragmentOrderListBinding
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.base.BaseFragment

class OrderListFragment : BaseFragment() {

    private val viewModel: OrdersViewModel by sharedViewModel()

    private var _binding: FragmentOrderListBinding? = null
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderListBinding.inflate(inflater, container, false)
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

    }

    private fun initObservers() {
        viewModel.orders.observe(viewLifecycleOwner, { result ->
            when (result) {
                is ResultEntity.Loading -> {

                }
                is ResultEntity.Error -> {

                }
                is ResultEntity.Success -> {

                }
            }
        })
    }
}