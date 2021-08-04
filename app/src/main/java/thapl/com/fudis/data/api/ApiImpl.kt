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
        private const val BASE_URL = "https://project95.account.thapl.com/"
    }

    private var service: ApiService

    init {
        val builder = OkHttpClient().newBuilder()
        builder.writeTimeout(30, TimeUnit.SECONDS)
        builder.readTimeout(30, TimeUnit.SECONDS)
        builder.connectTimeout(30, TimeUnit.SECONDS)

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

    override suspend fun auth(username: String?, password: String?) = service.auth(username, password)

}