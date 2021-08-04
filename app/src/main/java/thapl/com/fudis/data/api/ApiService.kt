package thapl.com.fudis.data.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import thapl.com.fudis.data.api.model.AuthResultApi

interface ApiService {

    @FormUrlEncoded
    @POST("user/login")
    suspend fun auth(
        @Field("username") username: String?,
        @Field("password") password: String?
    ): AuthResultApi

}