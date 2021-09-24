package thapl.com.fudis.ui.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import thapl.com.fudis.GlideApp
import thapl.com.fudis.databinding.FragmentOrderListBinding
import thapl.com.fudis.domain.model.ORDER_STATUS_NEW
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.adapters.ACTION
import thapl.com.fudis.ui.adapters.MORE
import thapl.com.fudis.ui.adapters.OrdersAdapter
import thapl.com.fudis.ui.base.BaseFragment

class OrderListFragment : BaseFragment() {

    private val viewModel: OrdersViewModel by sharedViewModel()

    private var _binding: FragmentOrderListBinding? = null
    private val binding get() = _binding

    private val glide by lazy { GlideApp.with(this) }

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
        binding?.vSwipeRefresh?.setOnRefreshListener {
            viewModel.refresh()
        }
        binding?.rvOrders?.adapter = OrdersAdapter(
            glide = glide,
            viewModel = viewModel,
            click = { item, type ->
                when (type) {
                    MORE -> {
                        navigate(OrderListFragmentDirections.actionOrder(item))
                    }
                    ACTION -> {
                        viewModel.changeStatus(item.id, item.getNextStatus())
                    }
                }
            }
        )
        binding?.rvOrders?.adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun initListeners() {

    }

    private fun initObservers() {
        viewModel.status.observe(viewLifecycleOwner, { result ->
            if (result is ResultEntity.Error) {
                Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.scrollUp.observe(viewLifecycleOwner, { result ->
            if (result == true) {
                binding?.rvOrders?.postDelayed({
                    binding?.rvOrders?.smoothScrollToPosition(0)
                }, 300)
            }
        })
        viewModel.orders.observe(viewLifecycleOwner, { result ->
            when (result) {
                is ResultEntity.Loading -> {
                    binding?.vSwipeRefresh?.isRefreshing = true
                }
                is ResultEntity.Error -> {
                    binding?.vSwipeRefresh?.isRefreshing = false
                    Log.d("okh", result.error.message)
                    Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
                }
                is ResultEntity.Success -> {
                    binding?.vSwipeRefresh?.isRefreshing = false
                    (binding?.rvOrders?.adapter as? OrdersAdapter)?.submitList(result.data)
                    if (result.data.any { it.status == ORDER_STATUS_NEW }) {
                        binding?.rvOrders?.postDelayed({
                            binding?.rvOrders?.smoothScrollToPosition(0)
                        }, 400)
                    }
                }
            }
        })
    }
}