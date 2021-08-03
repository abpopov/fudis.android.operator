package thapl.com.fudis.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import thapl.com.fudis.databinding.FragmentSplashBinding
import thapl.com.fudis.ui.base.BaseFragment

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
        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initObservers() {
        if (viewModel.isLoggedIn()) {
            navigate(SplashFragmentDirections.actionOrders())
        } else {
            navigate(SplashFragmentDirections.actionAuth())
        }
    }
}