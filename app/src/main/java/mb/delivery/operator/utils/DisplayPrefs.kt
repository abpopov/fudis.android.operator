package mb.delivery.operator.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import kotlin.math.roundToInt

/**
 * Отображение планшета: масштаб текста, масштаб UI, тема.
 * Prefs: naukoteka.prefs (как сессия).
 */
object DisplayPrefs {

    private const val PREFS_NAME = "naukoteka.prefs"
    private const val KEY_TEXT_SCALE = "TEXT_SCALE_V2"
    private const val KEY_UI_SCALE = "UI_SCALE_V2"
    private const val KEY_DARK = "THEME_DARK"
    /** Нативный DPI устройства — чтобы масштаб не «уплывал» при recreate. */
    private const val KEY_BASE_DPI = "BASE_DENSITY_DPI"

    private const val TEXT_MIN = 0.8f
    private const val TEXT_MAX = 1.5f
    private const val TEXT_DELTA = 0.05f

    private const val UI_MIN = 0.8f
    private const val UI_MAX = 1.5f
    private const val UI_DELTA = 0.1f

    private const val DEFAULT_SCALE = 1.0f

    fun wrap(base: Context): Context {
        val config = Configuration(base.resources.configuration)
        config.fontScale = textScale(base)
        val baseDpi = baseDensityDpi(base)
        val dpi = (baseDpi * uiScale(base)).toInt().coerceIn(120, 800)
        config.densityDpi = dpi
        return base.createConfigurationContext(config)
    }

    fun applyTheme(context: Context) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark(context)) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    // --- text ---

    fun textScale(context: Context): Float =
        snap(prefs(context).getFloat(KEY_TEXT_SCALE, DEFAULT_SCALE), TEXT_MIN, TEXT_MAX, TEXT_DELTA)

    fun textLabel(context: Context): String = percentLabel(textScale(context))

    fun canDecreaseText(context: Context): Boolean = textScale(context) > TEXT_MIN + 0.001f

    fun canIncreaseText(context: Context): Boolean = textScale(context) < TEXT_MAX - 0.001f

    fun decreaseText(context: Context): Boolean {
        val next = snap(textScale(context) - TEXT_DELTA, TEXT_MIN, TEXT_MAX, TEXT_DELTA)
        if (next == textScale(context)) return false
        prefs(context).edit().putFloat(KEY_TEXT_SCALE, next).apply()
        return true
    }

    fun increaseText(context: Context): Boolean {
        val next = snap(textScale(context) + TEXT_DELTA, TEXT_MIN, TEXT_MAX, TEXT_DELTA)
        if (next == textScale(context)) return false
        prefs(context).edit().putFloat(KEY_TEXT_SCALE, next).apply()
        return true
    }

    // --- UI ---

    fun uiScale(context: Context): Float =
        snap(prefs(context).getFloat(KEY_UI_SCALE, DEFAULT_SCALE), UI_MIN, UI_MAX, UI_DELTA)

    fun uiLabel(context: Context): String = percentLabel(uiScale(context))

    fun canDecreaseUi(context: Context): Boolean = uiScale(context) > UI_MIN + 0.001f

    fun canIncreaseUi(context: Context): Boolean = uiScale(context) < UI_MAX - 0.001f

    fun decreaseUi(context: Context): Boolean {
        val next = snap(uiScale(context) - UI_DELTA, UI_MIN, UI_MAX, UI_DELTA)
        if (next == uiScale(context)) return false
        prefs(context).edit().putFloat(KEY_UI_SCALE, next).apply()
        return true
    }

    fun increaseUi(context: Context): Boolean {
        val next = snap(uiScale(context) + UI_DELTA, UI_MIN, UI_MAX, UI_DELTA)
        if (next == uiScale(context)) return false
        prefs(context).edit().putFloat(KEY_UI_SCALE, next).apply()
        return true
    }

    // --- theme ---

    fun isDark(context: Context): Boolean =
        prefs(context).getBoolean(KEY_DARK, false)

    fun setDark(context: Context, dark: Boolean) {
        prefs(context).edit().putBoolean(KEY_DARK, dark).apply()
        applyTheme(context)
    }

    fun toggleDark(context: Context): Boolean {
        val next = !isDark(context)
        setDark(context, next)
        return next
    }

    private fun percentLabel(scale: Float): String =
        "${(scale * 100f).roundToInt()}%"

    private fun snap(value: Float, min: Float, max: Float, delta: Float): Float {
        val steps = ((value - min) / delta).roundToInt()
        return (min + steps * delta).coerceIn(min, max).let {
            // убрать артефакты float (0.9000001 → 0.9)
            (it * 100f).roundToInt() / 100f
        }
    }

    private fun baseDensityDpi(context: Context): Int {
        val p = prefs(context)
        val saved = p.getInt(KEY_BASE_DPI, 0)
        if (saved in 120..800) return saved
        val dpi = context.applicationContext.resources.displayMetrics.densityDpi
            .coerceIn(120, 800)
        p.edit().putInt(KEY_BASE_DPI, dpi).apply()
        return dpi
    }

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
