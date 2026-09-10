package mb.delivery.operator.data.prefs

interface Prefs {
    fun getUserToken(): String?
    fun setUserToken(value: String?)
    fun getRefreshToken(): String?
    fun setRefreshToken(value: String?)
    fun getOrganizationId(): Int
    fun setOrganizationId(value: Int?)
    fun getProjectId(): Int
    fun setProjectId(value: Int?)
    fun getCustomBaseUrl(): String?
    fun setCustomBaseUrl(value: String?)
    fun hasHostConfig(): Boolean
    fun getHostDisplayLabel(): String
    fun clearHostConfig()
    fun getApiBaseUrl(): String?
    fun getWsSocketUrl(): String?
    fun setWsSocketUrl(value: String?)
    fun getWsSocketChannel(): String?
    fun setWsSocketChannel(value: String?)
    fun getWsSocketToken(): String?
    fun setWsSocketToken(value: String?)
    fun clearSession()
    fun getOrganizationState(): Boolean
    fun setOrganizationState(value: Boolean?)
    fun getShowMenu(): Boolean
    fun setShowMenu(value: Boolean?)
    fun getShowOrders(): Boolean
    fun setShowOrders(value: Boolean?)
    fun getShowStopList(): Boolean
    fun setShowStopList(value: Boolean?)
    fun getShowHighload(): Boolean
    fun setShowHighload(value: Boolean?)
    fun getAllowStatusWithoutDishesReady(): Boolean
    fun setAllowStatusWithoutDishesReady(value: Boolean?)
    fun getAllowEditOrder(): Boolean
    fun setAllowEditOrder(value: Boolean?)
    fun getEnableManualPosExport(): Boolean
    fun setEnableManualPosExport(value: Boolean?)
}