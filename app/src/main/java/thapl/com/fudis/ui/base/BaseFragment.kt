package thapl.com.fudis.ui.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseFragment : Fragment() {

    protected fun navigate(directions: NavDirections) {
        lifecycleScope.launch {
            delay(150)
            try {
                findNavController().navigate(directions)
            } catch (e: Exception) {

            }
        }
    }

}