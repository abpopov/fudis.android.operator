package mb.delivery.operator.data.api

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mb.delivery.operator.BuildConfig
import mb.delivery.operator.data.api.model.AuthRequestApi
import mb.delivery.operator.data.api.model.OrderCartItemRequestApi
import mb.delivery.operator.data.api.model.OrderCartUpdateRequestApi
import mb.delivery.operator.data.api.model.OrderExportToPosRequestApi
import mb.delivery.operator.data.api.model.OrderStatusRequestApi
import mb.delivery.operator.data.api.model.OrganizationDropStatusRequestApi
import mb.delivery.operator.data.api.model.OrganizationSetStatusRequestApi
import mb.delivery.operator.data.api.model.RefreshTokenRequestApi
import mb.delivery.operator.data.api.model.SetProductStopRequestApi
import mb.delivery.operator.data.api.model.StatusRequestApi
import mb.delivery.operator.data.api.model.UserApi
import mb.delivery.operator.data.auth.AuthSession
import mb.delivery.operator.data.auth.SessionEvents
import mb.delivery.operator.data.prefs.Prefs
import mb.delivery.operator.notifications.OperatorWebSocket
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiImpl(
    gson: Gson,
    private val prefs: Prefs,
    private val sessionEvents: SessionEvents,
    private val socket: OperatorWebSocket
) : Api {

    companion object {
        private const val BASE_URL = "https://adm.thapl.com/"
        private const val TIMEOUT = 45L
    }

    private val refreshService: ApiService
    private val service: ApiService

    init {
        val refreshClient = buildClient(authenticator = null)
        refreshService = retrofit(gson, refreshClient).create(ApiService::class.java)

        val authenticator = TokenAuthenticator(
            prefs = prefs,
            sessionEvents = sessionEvents,
            socket = socket,
            refreshService = refreshService,
            refreshUrl = { "${getUrl()}/user/refresh-token" }
        )
        service = retrofit(gson, buildClient(authenticator)).create(ApiService::class.java)
    }

    private fun buildClient(authenticator: okhttp3.Authenticator?): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
        builder.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        builder.readTimeout(TIMEOUT, TimeUnit.SECONDS)
        builder.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        if (authenticator != null) {
            builder.authenticator(authenticator)
        }
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
        }
        return builder.build()
    }

    private fun retrofit(gson: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    private fun getUrl(): String {
        val base = prefs.getApiBaseUrl() ?: BASE_URL.trimEnd('/')
        return ProjectUrlBuilder.operatorApiUrl(base)
    }

    override suspend fun auth(
        username: String?,
        password: String?
    ) = service.auth("${getUrl()}/user/login", AuthRequestApi(username, password))

    override suspend fun refreshSession(): UserApi = withContext(Dispatchers.IO) {
        val refresh = prefs.getRefreshToken().orEmpty()
        if (refresh.isEmpty()) {
            throw IllegalStateException("No refresh token")
        }
        val response = refreshService.refreshToken(
            "${getUrl()}/user/refresh-token",
            RefreshTokenRequestApi(refresh)
        ).execute()
        val user = response.body()
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        if (user == null || user.accessToken.isNullOrEmpty()) {
            throw IllegalStateException("Empty access token")
        }
        AuthSession.applyUser(prefs, socket, sessionEvents, user)
        user
    }

    override suspend fun orders() = service.orders(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/order/index"
    )

    override suspend fun order(id: Long) = service.order(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/order/view",
        id
    )

    override suspend fun changeStatus(order: Long?, status: Int?) = service.changeStatus(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/order/change-status",
        OrderStatusRequestApi(id = order, status = status)
    )

    override suspend fun changeItemStatus(item: Int?, status: Int?) = service.changeItemStatus(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/order/change-cart-item-status",
        StatusRequestApi(id = item, status = status)
    )

    override suspend fun updateOrder(id: Long, clientComment: String?, organizationId: Int?): OrderApi {
        val body = linkedMapOf<String, Any?>("id" to id)
        if (clientComment != null) {
            body["client_comment"] = clientComment
        }
        if (organizationId != null) {
            body["organization_id"] = organizationId
        }
        return service.updateOrder(
            "Bearer ${prefs.getUserToken()}",
            "${getUrl()}/order/update",
            body
        )
    }

    override suspend fun updateCart(id: Long, cartItems: List<OrderCartItemRequestApi>) = service.updateCart(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/order/update-cart",
        OrderCartUpdateRequestApi(id = id, cartItems = cartItems)
    )

    override suspend fun exportToPos(id: Long) = service.exportToPos(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/order/export-to-pos",
        OrderExportToPosRequestApi(id = id)
    )

    override suspend fun editProducts(organizationId: Int) = service.editProducts(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/catalog/edit-products",
        organizationId
    )

    override suspend fun categories() = service.categories(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/catalog/get-menu"
    )

    override suspend fun menu() = service.menu(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/user/get-top-menu"
    )

    override suspend fun catalog(id: Long?) = service.catalog(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/catalog/get-catalog-items",
        id
    )

    override suspend fun products(organizationId: Int) = service.products(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/catalog/products",
        organizationId
    )

    override suspend fun stopList() = service.stopList(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/catalog/stop-list"
    )

    override suspend fun stopProduct(organizationId: Int, catalogItemId: Long, stop: Boolean) = service.stopProduct(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/catalog/set-product-stop",
        SetProductStopRequestApi(
            organizationId = organizationId,
            catalogItemId = catalogItemId,
            stop = stop
        )
    )

    override suspend fun receipt(id: Long?) = service.receipt(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/catalog/get-tech-card",
        id
    )

    override suspend fun organizations() = service.organizations(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/organization/index"
    )

    override suspend fun setOrganizationStatus(
        organizationId: Int,
        mode: String,
        cause: Int,
        dropAfterMinutes: Int,
        highLoadGap: Int?,
        stopCauseText: String?
    ) = service.setOrganizationStatus(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/organization/set-status",
        OrganizationSetStatusRequestApi(
            organizationId = organizationId,
            mode = mode,
            cause = cause,
            dropAfterMinutes = dropAfterMinutes,
            highLoadGap = highLoadGap,
            stopCauseText = stopCauseText
        )
    )

    override suspend fun dropOrganizationStatus(organizationId: Int) = service.dropOrganizationStatus(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/organization/drop-status",
        OrganizationDropStatusRequestApi(organizationId = organizationId)
    )
}
