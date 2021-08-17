package thapl.com.fudis.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import thapl.com.fudis.R

class FlarePlaceHolderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var bg: ImageView = ImageView(context)
    private var flare: ImageView = ImageView(context)

    private var anim: Animation? = null

    init {
        addView(bg, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        addView(flare, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        bg.setImageResource(R.drawable.ic_bg_rounded_bl05_12)
        bg.alpha = 0.5f
        flare.setImageResource(R.drawable.ic_bg_rounded_flare_12)
    }

    fun startAnim() {
        anim = TranslateAnimation(
            -(flare.width).toFloat(),
            (bg.width + flare.width).toFloat(),
            0f,
            0f
        )
        anim?.duration = 1200
        anim?.fillAfter = false
        anim?.repeatCount = Animation.INFINITE
        anim?.repeatMode = Animation.RESTART
        anim?.interpolator = AccelerateDecelerateInterpolator()
        flare.startAnimation(anim)
    }

    fun stopAnim() {
        anim?.cancel()
    }
}