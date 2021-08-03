package thapl.com.fudis.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import thapl.com.fudis.R
import thapl.com.fudis.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private var navController: NavController? = null

    private val viewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment) as? NavHostFragment
        navController = navHostFragment?.navController
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment -> {

                }
                R.id.authFragment -> {
                    window?.setBackgroundDrawableResource(R.drawable.ic_window_background_blank)
                }
                R.id.ordersFragment -> {
                    window?.setBackgroundDrawableResource(R.drawable.ic_window_background_blank)
                }
                R.id.stopsFragment -> {
                    window?.setBackgroundDrawableResource(R.drawable.ic_window_background_blank)
                }
            }
        }
    }

}