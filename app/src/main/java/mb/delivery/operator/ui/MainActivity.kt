package mb.delivery.operator.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import mb.delivery.operator.R
import mb.delivery.operator.data.auth.SessionEvents
import mb.delivery.operator.notifications.OperatorWebSocket
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private var navController: NavController? = null

    private val socket: OperatorWebSocket by inject()
    private val sessionEvents: SessionEvents by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment) as? NavHostFragment
        navController = navHostFragment?.navController
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment -> {

                }
                R.id.projectFragment -> {
                    window?.setBackgroundDrawableResource(R.drawable.ic_window_background_blank)
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
        sessionEvents.expired.observe(this) {
            val current = navController?.currentDestination?.id
            if (current == R.id.splashFragment ||
                current == R.id.authFragment ||
                current == R.id.projectFragment
            ) {
                return@observe
            }
            try {
                navController?.navigate(R.id.action_logout)
            } catch (_: Exception) {
            }
        }
    }

    override fun onStart() {
        super.onStart()
        socket.start()
    }

    override fun onStop() {
        socket.stop()
        super.onStop()
    }
}
