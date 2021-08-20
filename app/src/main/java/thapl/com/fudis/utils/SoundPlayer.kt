package thapl.com.fudis.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import thapl.com.fudis.R
import java.io.IOException

class SoundPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun start() {
        val soundId: Int = R.raw.order
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .build()
        )
        mediaPlayer?.isLooping = false
        mediaPlayer?.setOnCompletionListener { stop() }
        val packageName = context.packageName
        val dataUri = Uri.parse("android.resource://$packageName/$soundId")
        try {
            mediaPlayer?.setDataSource(context, dataUri)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, e)
        } catch (e: SecurityException) {
            Log.w(TAG, e)
        } catch (e: IllegalStateException) {
            Log.w(TAG, e)
        } catch (e: IOException) {
            Log.w(TAG, e)
        }
    }

    fun stop() {
        if (mediaPlayer == null) return
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        private val TAG = SoundPlayer::class.java.simpleName
    }

}