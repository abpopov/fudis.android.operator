package thapl.com.fudis.data.api

import retrofit2.http.*
import thapl.com.fudis.data.api.model.*

interface ApiService {

    @FormUrlEncoded
    @POST
    suspend fun auth(
        @Url url: String,
        @Field("username") username: String?,
        @Field("password") password: String?
    ): AuthResultApi

    @GET
    suspend fun orders(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Query("page") page: Int?,
        @Query("per-page") limit: Int?
    ): List<OrderApi>

    @FormUrlEncoded
    @POST
    suspend fun changeStatus(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Field("id") order: Long?,
        @Field("status") status: Int?
    ): StatusApi

    @GET
    suspend fun receipt(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Query("id") id: Long?
    ): ReceiptApi

    @GET
    suspend fun categories(
        @Header("Authorization") token: String?,
        @Url url: String
    ): List<CategoryApi>

    @GET
    suspend fun catalog(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Query("category_id") id: Long?
    ): List<CatalogApi>

    @GET
    suspend fun products(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Query("organization_id") id: Int?
    ): List<ProductApi>

    @FormUrlEncoded
    @POST
    suspend fun stopProduct(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Field("organization_id") id: Int?,
        @Field("product_id") product: Long?,
        @Field("action") action: Int?
    ): SuccessApi

    @FormUrlEncoded
    @POST
    suspend fun stopOrganization(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Field("organization_id") id: Int?,
        @Field("drop_time") time: Int?,
        @Field("cause") cause: Int?
    ): SuccessApi

    @FormUrlEncoded
    @POST
    suspend fun startOrganization(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Field("organization_id") id: Int?
    ): SuccessApi

}