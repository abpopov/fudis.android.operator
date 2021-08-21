package thapl.com.fudis.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import thapl.com.fudis.R

class SoundPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    var isPlaying = false
        private set

    fun start(loop: Boolean = false) {
        mediaPlayer?.release()
        val soundId: Int = R.raw.order
        val packageName = context.packageName
        val dataUri = Uri.parse("android.resource://$packageName/$soundId")
        isPlaying = try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                isLooping = loop
                setOnCompletionListener {
                    if (loop.not()) {
                        stop()
                    }
                }
                setOnErrorListener { _, i, i2 ->
                    Toast.makeText(context, "Код ошибки $i, $i2", Toast.LENGTH_SHORT).show()
                    true
                }
                setDataSource(context, dataUri)
                setScreenOnWhilePlaying(true)
                prepare()
                start()
            }
            true
        } catch (e: Exception) {
            Log.w(TAG, e)
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun stop() {
        if (mediaPlayer == null) return
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }

    companion object {
        private val TAG = SoundPlayer::class.java.simpleName
    }

}