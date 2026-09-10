package mb.delivery.operator.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import mb.delivery.operator.databinding.FragmentSplashBinding
import mb.delivery.operator.domain.model.SplashDestination
import mb.delivery.operator.ui.base.BaseFragment

class SplashFragment : BaseFragment() {

    private val viewModel: SplashViewModel by sharedViewModel()

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.destination.observe(viewLifecycleOwner) { dest ->
            when (dest) {
                SplashDestination.ORDERS -> navigate(SplashFragmentDirections.actionOrders())
                SplashDestination.AUTH -> navigate(SplashFragmentDirections.actionAuth())
                SplashDestination.PROJECT -> navigate(SplashFragmentDirections.actionProject())
            }
        }
        viewModel.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
