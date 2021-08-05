package thapl.com.fudis.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.Navigation
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import thapl.com.fudis.R
import thapl.com.fudis.databinding.FragmentOrdersBinding
import thapl.com.fudis.ui.base.BaseFragment

class OrdersFragment : BaseFragment() {

    private val viewModel: OrdersViewModel by sharedViewModel()

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding

    private val navController: NavController? by lazy {
        binding?.ordersFragment?.let {
            Navigation.findNavController(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(SOURCE_MENU) { key, bundle ->
            bundle.getInt(key).let {
                viewModel.selectMenu(it)
            }
        }
    }

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

    override fun onResume() {
        super.onResume()
        viewModel.selectMenu(0)
    }

    private fun initViews() {

    }

    private fun initListeners() {
        binding?.tvOrders?.setOnClickListener {
            viewModel.selectMenu(0)
            navController?.navigate(R.id.orderListFragment)
        }
        binding?.tvPause?.setOnClickListener {
            val source = viewModel.menuPos.value ?: 0
            viewModel.selectMenu(1)
            navigate(OrdersFragmentDirections.actionPause(source))
        }
        binding?.tvStops?.setOnClickListener {
            viewModel.selectMenu(2)
            navController?.navigate(R.id.stopsFragment)
        }
        binding?.tvHelp?.setOnClickListener {
            val source = viewModel.menuPos.value ?: 0
            viewModel.selectMenu(3)
            navigate(OrdersFragmentDirections.actionHelp(source))
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

    companion object {
        const val SOURCE_MENU = "source_menu"
    }
}