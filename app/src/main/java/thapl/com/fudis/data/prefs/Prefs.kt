package thapl.com.fudis.data.prefs

interface Prefs {
    fun getUserToken(): String?
    fun setUserToken(value: String?)
}