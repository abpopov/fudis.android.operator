package thapl.com.fudis.ui.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.Navigation
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import thapl.com.fudis.R
import thapl.com.fudis.databinding.FragmentOrdersBinding
import thapl.com.fudis.domain.model.ORDER_STATUS_NEW
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.base.BaseFragment
import thapl.com.fudis.ui.categories.CategoriesViewModel
import thapl.com.fudis.ui.dialogs.PauseViewModel
import thapl.com.fudis.utils.SoundPlayer

class OrdersFragment : BaseFragment() {

    private val viewModel: OrdersViewModel by sharedViewModel()
    private val pauseViewModel: PauseViewModel by sharedViewModel()
    private val catsViewModel: CategoriesViewModel by sharedViewModel()

    private var soundPlayer: SoundPlayer? = null
    private var anim: Animation? = null

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
            catsViewModel.search.postValue("")
        }
        binding?.tvCats?.setOnClickListener {
            viewModel.selectMenu(2)
            navController?.navigate(R.id.categoriesFragment)
        }
        binding?.tvStops?.setOnClickListener {
            viewModel.selectMenu(3)
            navController?.navigate(R.id.stopsFragment)
            catsViewModel.search.postValue("")
        }
        binding?.tvHelp?.setOnClickListener {
            val source = viewModel.menuPos.value ?: 0
            viewModel.selectMenu(4)
            navigate(OrdersFragmentDirections.actionHelp(source))
        }
    }

    private fun initObservers() {
        viewModel.menuPos.observe(viewLifecycleOwner, { pos ->
            binding?.tvOrders?.isChecked = pos == 0
            binding?.tvPause?.isChecked = pos == 1
            binding?.tvCats?.isChecked = pos == 2
            binding?.tvStops?.isChecked = pos == 3
            binding?.tvHelp?.isChecked = pos == 4
            binding?.tvOrders?.isEnabled = pos != 0
            binding?.tvPause?.isEnabled = pos != 1
            binding?.tvCats?.isEnabled = pos != 2
            binding?.tvStops?.isEnabled = pos != 3
            binding?.tvHelp?.isEnabled = pos != 4
        })
        pauseViewModel.pauseRequest.observe(viewLifecycleOwner, { result ->
            when (result) {
                is ResultEntity.Loading -> {

                }
                is ResultEntity.Error -> {
                    result.error.message.let {
                        if (it.isEmpty().not()) {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is ResultEntity.Success -> {
                    pauseViewModel.working.postValue(result.data)
                }
            }
        })
        pauseViewModel.working.observe(viewLifecycleOwner, { working ->
            binding?.tvPause?.setOnClickListener {
                val source = viewModel.menuPos.value ?: 0
                viewModel.selectMenu(1)
                if (working == true) {
                    navigate(OrdersFragmentDirections.actionPause(source))
                } else {
                    navigate(OrdersFragmentDirections.actionStart(source))
                }
            }
        })
        viewModel.orders.observe(viewLifecycleOwner, { result ->
            when (result) {
                is ResultEntity.Success -> {
                    if (result.data.any {
                            it.status == ORDER_STATUS_NEW
                        }) {
                        soundPlayer?.stop()
                        soundPlayer = SoundPlayer(requireContext())
                        soundPlayer?.start(true)
                        setAlert(true)
                    } else {
                        soundPlayer?.stop()
                        setAlert(false)
                    }
                }
                else -> {
                    soundPlayer?.stop()
                    setAlert(false)
                }
            }
        })
    }

    private fun setAlert(start: Boolean) {
        Log.d("okh", "setAlert $start")
        if (start) {
            binding?.vAlert?.visibility = View.VISIBLE
            anim = ScaleAnimation(
                0.4f, 1.6f,
                0.4f, 1.6f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            anim?.interpolator = AccelerateInterpolator()
            anim?.fillAfter = false
            anim?.duration = 600
            anim?.repeatCount = Animation.INFINITE
            anim?.repeatMode = Animation.REVERSE
            binding?.vAlert?.startAnimation(anim)
        } else {
            anim?.cancel()
            binding?.vAlert?.visibility = View.GONE
        }
    }

    companion object {
        const val SOURCE_MENU = "source_menu"
    }
}