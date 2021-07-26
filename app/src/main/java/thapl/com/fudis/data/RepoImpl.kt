package thapl.com.fudis.data

import android.content.Context
import thapl.com.fudis.data.api.Api
import thapl.com.fudis.data.local.Local
import thapl.com.fudis.data.prefs.Prefs

class RepoImpl(
    private val ctx: Context,
    private val api: Api,
    private val db: Local,
    private val prefs: Prefs
) : Repo {

    override fun getContext(): Context = ctx

    // prefs

    override fun getUserToken() = prefs.getUserToken()

    override fun setUserToken(value: String?) {
        prefs.setUserToken(value)
    }
}