package thapl.com.fudis.data.prefs

interface Prefs {
    fun getUserToken(): String?
    fun setUserToken(value: String?)
    fun getOrganizationId(): Int
    fun setOrganizationId(value: Int?)
    fun getOrganizationState(): Boolean
    fun setOrganizationState(value: Boolean?)
}