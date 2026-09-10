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
        private const val REFRESH_TOKEN = "REFRESH_TOKEN"
        private const val PROJECT_ID = "PROJECT_ID"
        private const val CUSTOM_BASE_URL = "CUSTOM_BASE_URL"
        private const val WS_SOCKET_URL = "WS_SOCKET_URL"
        private const val WS_SOCKET_CHANNEL = "WS_SOCKET_CHANNEL"
        private const val WS_SOCKET_TOKEN = "WS_SOCKET_TOKEN"
        private const val ORGANIZATION_ID = "ORGANIZATION_ID"
        private const val ORGANIZATION_STATE = "ORGANIZATION_STATE"
        private const val SHOW_MENU = "SHOW_MENU"
        private const val SHOW_ORDERS = "SHOW_ORDERS"
        private const val SHOW_STOP_LIST = "SHOW_STOP_LIST"
        private const val SHOW_HIGHLOAD = "SHOW_HIGHLOAD"
        private const val ALLOW_STATUS_WITHOUT_DISHES_READY = "ALLOW_STATUS_WITHOUT_DISHES_READY"
        private const val ALLOW_EDIT_ORDER = "ALLOW_EDIT_ORDER"
        private const val ENABLE_MANUAL_POS_EXPORT = "ENABLE_MANUAL_POS_EXPORT"
    }

    private var prefs: SharedPreferences = ctx.getSharedPreferences("naukoteka.prefs", Context.MODE_PRIVATE)

    override fun getUserToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    override fun setUserToken(value: String?) {
        putString(USER_TOKEN, value)
    }

    override fun getRefreshToken(): String? {
        return getString(REFRESH_TOKEN)
    }

    override fun setRefreshToken(value: String?) {
        putString(REFRESH_TOKEN, value)
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

    override fun getCustomBaseUrl(): String? {
        return getString(CUSTOM_BASE_URL)
    }

    override fun setCustomBaseUrl(value: String?) {
        putString(CUSTOM_BASE_URL, value)
    }

    override fun hasHostConfig(): Boolean {
        return getProjectId() > 0 || !getCustomBaseUrl().isNullOrEmpty()
    }

    override fun getHostDisplayLabel(): String {
        val custom = getCustomBaseUrl()
        if (!custom.isNullOrEmpty()) {
            return custom
        }
        val projectId = getProjectId()
        return if (projectId > 0) projectId.toString() else ""
    }

    override fun clearHostConfig() {
        setProjectId(null)
        setCustomBaseUrl(null)
    }

    override fun getApiBaseUrl(): String? {
        val custom = getCustomBaseUrl()
        if (!custom.isNullOrEmpty()) {
            return custom
        }
        val projectId = getProjectId()
        if (projectId <= 0) {
            return null
        }
        return "https://project$projectId.adm.thapl.com"
    }

    override fun getWsSocketUrl(): String? {
        return getString(WS_SOCKET_URL)
    }

    override fun setWsSocketUrl(value: String?) {
        putString(WS_SOCKET_URL, value)
    }

    override fun getWsSocketChannel(): String? {
        return getString(WS_SOCKET_CHANNEL)
    }

    override fun setWsSocketChannel(value: String?) {
        putString(WS_SOCKET_CHANNEL, value)
    }

    override fun getWsSocketToken(): String? {
        return getString(WS_SOCKET_TOKEN)
    }

    override fun setWsSocketToken(value: String?) {
        putString(WS_SOCKET_TOKEN, value)
    }

    override fun clearSession() {
        setUserToken(null)
        setRefreshToken(null)
        setWsSocketUrl(null)
        setWsSocketChannel(null)
        setWsSocketToken(null)
    }

    private fun getString(key: String): String? {
        return prefs.getString(key, null)?.takeIf { it.isNotEmpty() }
    }

    private fun putString(key: String, value: String?) {
        prefs.edit().also {
            if (value.isNullOrEmpty()) {
                it.remove(key)
            } else {
                it.putString(key, value)
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

    override fun getAllowStatusWithoutDishesReady(): Boolean {
        return prefs.getBoolean(ALLOW_STATUS_WITHOUT_DISHES_READY, false)
    }

    override fun setAllowStatusWithoutDishesReady(value: Boolean?) {
        prefs.edit().also {
            if (value == null) {
                it.remove(ALLOW_STATUS_WITHOUT_DISHES_READY)
            } else {
                it.putBoolean(ALLOW_STATUS_WITHOUT_DISHES_READY, value)
            }
        }.apply()
    }

    override fun getAllowEditOrder(): Boolean {
        return prefs.getBoolean(ALLOW_EDIT_ORDER, false)
    }

    override fun setAllowEditOrder(value: Boolean?) {
        prefs.edit().also {
            if (value == null) {
                it.remove(ALLOW_EDIT_ORDER)
            } else {
                it.putBoolean(ALLOW_EDIT_ORDER, value)
            }
        }.apply()
    }

    override fun getEnableManualPosExport(): Boolean {
        return prefs.getBoolean(ENABLE_MANUAL_POS_EXPORT, false)
    }

    override fun setEnableManualPosExport(value: Boolean?) {
        prefs.edit().also {
            if (value == null) {
                it.remove(ENABLE_MANUAL_POS_EXPORT)
            } else {
                it.putBoolean(ENABLE_MANUAL_POS_EXPORT, value)
            }
        }.apply()
    }
}