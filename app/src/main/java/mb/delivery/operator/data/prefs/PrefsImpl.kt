package mb.delivery.operator.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class PrefsImpl(
    ctx: Context,
    val gson: Gson
) : Prefs {

    companion object {
        private const val USER_TOKEN = "USER_TOKEN"
        private const val PROJECT_ID = "PROJECT_ID"
        private const val ORGANIZATION_ID = "ORGANIZATION_ID"
        private const val ORGANIZATION_STATE = "ORGANIZATION_STATE"
        private const val SHOW_MENU = "SHOW_MENU"
        private const val SHOW_ORDERS = "SHOW_ORDERS"
        private const val SHOW_STOP_LIST = "SHOW_STOP_LIST"
        private const val SHOW_HIGHLOAD = "SHOW_HIGHLOAD"
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

    override fun getProjectId(): Int {
        return prefs.getInt(PROJECT_ID, -1)
    }

    override fun setProjectId(value: Int?) {
        prefs.edit().also {
            if (value == null) {
                it.remove(PROJECT_ID)
            } else {
                it.putInt(PROJECT_ID, value)
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

    override fun getShowMenu(): Boolean {
        return prefs.getBoolean(SHOW_MENU, true)
    }

    override fun setShowMenu(value: Boolean?) {
        prefs.edit().also {
            if (value == null) {
                it.remove(SHOW_MENU)
            } else {
                it.putBoolean(SHOW_MENU, value)
            }
        }.apply()
    }

    override fun getShowOrders(): Boolean {
        return prefs.getBoolean(SHOW_ORDERS, true)
    }

    override fun setShowOrders(value: Boolean?) {
        prefs.edit().also {
            if (value == null) {
                it.remove(SHOW_ORDERS)
            } else {
                it.putBoolean(SHOW_ORDERS, value)
            }
        }.apply()
    }

    override fun getShowStopList(): Boolean {
        return prefs.getBoolean(SHOW_STOP_LIST, true)
    }

    override fun setShowStopList(value: Boolean?) {
        prefs.edit().also {
            if (value == null) {
                it.remove(SHOW_STOP_LIST)
            } else {
                it.putBoolean(SHOW_STOP_LIST, value)
            }
        }.apply()
    }

    override fun getShowHighload(): Boolean {
        return prefs.getBoolean(SHOW_HIGHLOAD, true)
    }

    override fun setShowHighload(value: Boolean?) {
        prefs.edit().also {
            if (value == null) {
                it.remove(SHOW_HIGHLOAD)
            } else {
                it.putBoolean(SHOW_HIGHLOAD, value)
            }
        }.apply()
    }
}