package thapl.com.fudis.data.api

import retrofit2.http.*
import thapl.com.fudis.data.api.model.AuthResultApi
import thapl.com.fudis.data.api.model.OrderApi

interface ApiService {

    @FormUrlEncoded
    @POST("user/login")
    suspend fun auth(
        @Field("username") username: String?,
        @Field("password") password: String?
    ): AuthResultApi

    @GET("order/get-list")
    suspend fun orders(
        @Header("Authorization") token: String?
    ): List<OrderApi>

}