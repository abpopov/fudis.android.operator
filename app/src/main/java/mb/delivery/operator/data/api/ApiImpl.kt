package mb.delivery.operator.data.api

import android.content.Context
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import mb.delivery.operator.BuildConfig
import mb.delivery.operator.data.api.model.StatusRequestApi
import mb.delivery.operator.data.prefs.Prefs
import java.util.concurrent.TimeUnit

class ApiImpl(
    ctx: Context,
    gson: Gson,
    private val prefs: Prefs
) : Api {

    companion object {
        private const val BASE_URL = "https://project481.serv.thapl.com/operator/"
        private const val BASE_URL_1 = "https://project"
        private const val BASE_URL_2 = ".serv.thapl.com/operator"
        private const val TIMEOUT = 45L
    }

    private var service: ApiService

    init {
        val builder = OkHttpClient().newBuilder()
        builder.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        builder.readTimeout(TIMEOUT, TimeUnit.SECONDS)
        builder.connectTimeout(TIMEOUT, TimeUnit.SECONDS)

        builder.addInterceptor { chain ->
            val request = chain.request().newBuilder()
            //request.addHeader("Accept", "application/json;charset=UTF-8")
            chain.proceed(request.build())
        }

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(builder.build())
            .build()

        service = retrofit.create(ApiService::class.java)
    }

    private fun getUrl(): String {
        return BASE_URL_1 + prefs.getProjectId() + BASE_URL_2
    }

    override suspend fun auth(
        username: String?,
        password: String?
    ) = service.auth("${getUrl()}/user/token", username, password)

    override suspend fun orders() = service.orders(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/order/get-list",
        1,
        100
    )

    override suspend fun changeStatus(order: Long?, status: Int?) = service.changeStatus(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/order/change-status",
        order,
        status
    )

    override suspend fun changeItemStatus(item: Int?, status: Int?) = service.changeItemStatus(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/order/change-cart-item-status",
        StatusRequestApi(id = item, status = status)
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

    override suspend fun products(id: Int?) = service.products(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/catalog/get-products",
        id
    )

    override suspend fun stopProduct(id: Int?, product: Long?, stop: Boolean?) = service.stopProduct(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/catalog/set-product-stop",
        id,
        product,
        if (stop == true) 1 else 0
    )

    override suspend fun receipt(id: Long?) = service.receipt(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/catalog/get-tech-card",
        id
    )

    override suspend fun stopOrganization(id: Int?, time: Int?, cause: Int?) = service.stopOrganization(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/organizations/add-stop",
        id,
        time,
        cause
    )

    override suspend fun startOrganization(id: Int?) = service.startOrganization(
        "Bearer ${prefs.getUserToken()}",
        "${getUrl()}/organizations/drop-stop",
        id
    )
}