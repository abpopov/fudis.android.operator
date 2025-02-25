package thapl.com.fudis.data.prefs

interface Prefs {
    fun getUserToken(): String?
    fun setUserToken(value: String?)
    fun getOrganizationId(): Int
    fun setOrganizationId(value: Int?)
    fun getProjectId(): Int
    fun setProjectId(value: Int?)
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
}