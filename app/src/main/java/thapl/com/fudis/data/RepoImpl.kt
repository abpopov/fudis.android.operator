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

    override fun getOrganizationId() = prefs.getOrganizationId()

    override fun setOrganizationId(value: Int?) {
        prefs.setOrganizationId(value)
    }

    override fun getOrganizationState() = prefs.getOrganizationState()

    override fun setOrganizationState(value: Boolean?) {
        prefs.setOrganizationState(value)
    }

    // api

    override suspend fun auth(username: String?, password: String?) = api.auth(username, password)

    override suspend fun orders() = api.orders()

    override suspend fun changeStatus(order: Long?, status: Int?) = api.changeStatus(order, status)

    override suspend fun categories() = api.categories()

    override suspend fun catalog(id: Long?) = api.catalog(id)

    override suspend fun products(id: Int?) = api.products(id)

    override suspend fun stopProduct(id: Int?, product: Long?, stop: Boolean?) = api.stopProduct(id, product, stop)

    override suspend fun receipt(id: Long?) = api.receipt(id)

    override suspend fun stopOrganization(id: Int?, time: Int?, cause: Int?) = api.stopOrganization(id, time, cause)

    override suspend fun startOrganization(id: Int?) = api.startOrganization(id)
}