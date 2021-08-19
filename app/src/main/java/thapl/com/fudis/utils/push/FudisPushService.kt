package thapl.com.fudis.utils.push

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import thapl.com.fudis.BuildConfig
import thapl.com.fudis.R
import thapl.com.fudis.domain.case.OrdersUseCase

@Suppress("SameParameterValue")
class FudisPushService : FirebaseMessagingService(), KoinComponent {

    private val useCase: OrdersUseCase by inject()

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("PUSH", "new token $p0")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("PUSH", "data ${message.data.entries.joinToString { "${it.key} - ${it.value}," }}")
        val text = message.data["message"] ?: ""
        val data = message.data["data"]
    }

    private fun sendNotification(text: String, target: String, targetId: Long?) {
        val channelName = "${BuildConfig.APPLICATION_ID}.channel"
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(this, channelName)
        } else {
            ""
        }
        val pendingIntent = getPushIntent(target, targetId)
        val notBuilder = NotificationCompat.Builder(this, channelId)
        notBuilder.setSmallIcon(R.drawable.ic_fudis_logo_v_w)
            .setTicker(text)
            .setWhen(System.currentTimeMillis())
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE or NotificationCompat.DEFAULT_LIGHTS)
        notBuilder.priority = NotificationCompat.PRIORITY_MAX
        notBuilder.setDefaults(Notification.DEFAULT_SOUND)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            notBuilder.setContentTitle(getString(R.string.app_name))
        }
        notBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(text))
        notBuilder.setAutoCancel(true)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(1000, notBuilder.build())
    }

    private fun getPushIntent(target: String, targetId: Long?): PendingIntent {
        return NavDeepLinkBuilder(this)
            .setGraph(R.navigation.fudis)
            .setDestination(R.id.splashFragment)
            .setArguments(Bundle().apply {

            })
            .createPendingIntent()
    }

    @TargetApi(26)
    @Synchronized
    private fun createNotificationChannel(context: Context, name: String): String {
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(name, "Сервис обработки заказов ${getString(R.string.app_name)}", importance)

        mChannel.description = "Настройки системы оповещения входящих PUSH-сообщений ${getString(R.string.app_name)}"
        mChannel.lightColor = ContextCompat.getColor(this, R.color.red)
        mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        mChannel.enableLights(true)
        mChannel.enableVibration(true)
        mNotificationManager?.createNotificationChannel(mChannel)
        return name
    }
}