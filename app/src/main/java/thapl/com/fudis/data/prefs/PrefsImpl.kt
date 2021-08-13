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
        private const val ORGANIZATION_ID = "ORGANIZATION_ID"
        private const val ORGANIZATION_STATE = "ORGANIZATION_STATE"
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

    override fun getOrganizationId(): Int {
        return prefs.getInt(ORGANIZATION_ID, 1)
    }

    override fun setOrganizationId(value: Int?) {
        prefs.edit().also {
            if (value == null) {
                it.remove(ORGANIZATION_ID)
            } else {
                it.putInt(ORGANIZATION_ID, value)
            }
        }.apply()
    }

    override fun getOrganizationState(): Boolean {
        return prefs.getBoolean(ORGANIZATION_STATE, true)
    }

    override fun setOrganizationState(value: Boolean?) {
        prefs.edit().also {
            if (value == null) {
                it.remove(ORGANIZATION_STATE)
            } else {
                it.putBoolean(ORGANIZATION_STATE, value)
            }
        }.apply()
    }
}