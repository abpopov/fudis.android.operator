package thapl.com.fudis.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class PrefsImpl(
    ctx: Context,
    val gson: Gson
) : Prefs {

    companion object {
        private const val USER_TOKEN = "USER_TOKEN"
    }

    private var prefs: SharedPreferences = ctx.getSharedPreferences("naukoteka.prefs", Context.MODE_PRIVATE)

    override fun getUserToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    override fun setUserToken(value: String?) {
        prefs.edit().also {
            if (value == null) {
                it.remove(USER_TOKEN)
            } else {
                it.putString(USER_TOKEN, value)
            }
        }.apply()
    }


}