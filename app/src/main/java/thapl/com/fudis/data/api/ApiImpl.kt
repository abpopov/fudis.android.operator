package thapl.com.fudis.data.api

import android.content.Context
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import thapl.com.fudis.BuildConfig
import thapl.com.fudis.data.prefs.Prefs
import java.util.concurrent.TimeUnit

class ApiImpl(
    ctx: Context,
    gson: Gson,
    private val prefs: Prefs
) : Api {

    companion object {
        private const val BASE_URL = "https://account.fudis.thapl.com/"
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

    override suspend fun auth(
        username: String?,
        password: String?
    ) = service.auth(username, password)

    override suspend fun orders() = service.orders(
        "Bearer ${prefs.getUserToken()}",
        1,
        100
    )

    override suspend fun changeStatus(order: Long?, status: Int?) = service.changeStatus(
        "Bearer ${prefs.getUserToken()}",
        order,
        status
    )

    override suspend fun categories() = service.categories(
        "Bearer ${prefs.getUserToken()}"
    )

    override suspend fun catalog(id: Long?) = service.catalog(
        "Bearer ${prefs.getUserToken()}",
        id
    )

    override suspend fun products(id: Int?) = service.products(
        "Bearer ${prefs.getUserToken()}",
        id
    )

    override suspend fun stopProduct(id: Int?, product: Long?, stop: Boolean?) = service.stopProduct(
        "Bearer ${prefs.getUserToken()}",
        id,
        product,
        if (stop == true) 1 else 0
    )

    override suspend fun receipt(id: Long?) = service.receipt(
        "Bearer ${prefs.getUserToken()}",
        id
    )

    override suspend fun stopOrganization(id: Int?, time: Int?, cause: Int?) = service.stopOrganization(
        "Bearer ${prefs.getUserToken()}",
        id,
        time,
        cause
    )

    override suspend fun startOrganization(id: Int?) = service.startOrganization(
        "Bearer ${prefs.getUserToken()}",
        id
    )
}