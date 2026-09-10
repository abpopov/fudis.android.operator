package mb.delivery.operator.data

import android.content.Context
import mb.delivery.operator.data.api.Api
import mb.delivery.operator.data.api.model.OrderCartItemRequestApi
import mb.delivery.operator.data.local.Local
import mb.delivery.operator.data.prefs.Prefs

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

    override fun getProjectId() = prefs.getProjectId()

    override fun setProjectId(value: Int?) {
        prefs.setProjectId(value)
    }

    override fun getCustomBaseUrl() = prefs.getCustomBaseUrl()

    override fun setCustomBaseUrl(value: String?) {
        prefs.setCustomBaseUrl(value)
    }

    override fun hasHostConfig() = prefs.hasHostConfig()

    override fun getHostDisplayLabel() = prefs.getHostDisplayLabel()

    override fun clearHostConfig() {
        prefs.clearHostConfig()
    }

    override fun getApiBaseUrl() = prefs.getApiBaseUrl()

    override fun getRefreshToken() = prefs.getRefreshToken()

    override fun setRefreshToken(value: String?) {
        prefs.setRefreshToken(value)
    }

    override fun getWsSocketUrl() = prefs.getWsSocketUrl()

    override fun setWsSocketUrl(value: String?) {
        prefs.setWsSocketUrl(value)
    }

    override fun getWsSocketChannel() = prefs.getWsSocketChannel()

    override fun setWsSocketChannel(value: String?) {
        prefs.setWsSocketChannel(value)
    }

    override fun getWsSocketToken() = prefs.getWsSocketToken()

    override fun setWsSocketToken(value: String?) {
        prefs.setWsSocketToken(value)
    }

    override fun clearSession() {
        prefs.clearSession()
    }

    override fun getOrganizationState() = prefs.getOrganizationState()

    override fun setOrganizationState(value: Boolean?) {
        prefs.setOrganizationState(value)
    }

    override fun getShowMenu() = prefs.getShowMenu()

    override fun setShowMenu(value: Boolean?) {
        prefs.setShowMenu(value)
    }

    override fun getShowOrders() = prefs.getShowOrders()

    override fun setShowOrders(value: Boolean?) {
        prefs.setShowOrders(value)
    }

    override fun getShowStopList() = prefs.getShowStopList()

    override fun setShowStopList(value: Boolean?) {
        prefs.setShowStopList(value)
    }

    override fun getShowHighload() = prefs.getShowHighload()

    override fun setShowHighload(value: Boolean?) {
        prefs.setShowHighload(value)
    }

    override fun getAllowStatusWithoutDishesReady() = prefs.getAllowStatusWithoutDishesReady()

    override fun setAllowStatusWithoutDishesReady(value: Boolean?) {
        prefs.setAllowStatusWithoutDishesReady(value)
    }

    override fun getAllowEditOrder() = prefs.getAllowEditOrder()

    override fun setAllowEditOrder(value: Boolean?) {
        prefs.setAllowEditOrder(value)
    }

    override fun getEnableManualPosExport() = prefs.getEnableManualPosExport()

    override fun setEnableManualPosExport(value: Boolean?) {
        prefs.setEnableManualPosExport(value)
    }

    // api

    override suspend fun auth(username: String?, password: String?) = api.auth(username, password)

    override suspend fun refreshSession() = api.refreshSession()

    override suspend fun orders() = api.orders()

    override suspend fun order(id: Long) = api.order(id)

    override suspend fun changeStatus(order: Long?, status: Int?) = api.changeStatus(order, status)

    override suspend fun changeItemStatus(item: Int?, status: Int?) = api.changeItemStatus(item, status)

    override suspend fun updateOrder(id: Long, clientComment: String?, organizationId: Int?) =
        api.updateOrder(id, clientComment, organizationId)

    override suspend fun updateCart(id: Long, cartItems: List<OrderCartItemRequestApi>) =
        api.updateCart(id, cartItems)

    override suspend fun exportToPos(id: Long) = api.exportToPos(id)

    override suspend fun editProducts(organizationId: Int) = api.editProducts(organizationId)

    override suspend fun categories() = api.categories()

    override suspend fun menu() = api.menu()

    override suspend fun catalog(id: Long?) = api.catalog(id)

    override suspend fun products(organizationId: Int) = api.products(organizationId)

    override suspend fun stopList() = api.stopList()

    override suspend fun stopProduct(organizationId: Int, catalogItemId: Long, stop: Boolean) =
        api.stopProduct(organizationId, catalogItemId, stop)

    override suspend fun receipt(id: Long?) = api.receipt(id)

    override suspend fun organizations() = api.organizations()

    override suspend fun setOrganizationStatus(
        organizationId: Int,
        mode: String,
        cause: Int,
        dropAfterMinutes: Int,
        highLoadGap: Int?,
        stopCauseText: String?
    ) = api.setOrganizationStatus(organizationId, mode, cause, dropAfterMinutes, highLoadGap, stopCauseText)

    override suspend fun dropOrganizationStatus(organizationId: Int) = api.dropOrganizationStatus(organizationId)
}