package thapl.com.fudis.data.api

import retrofit2.http.*
import thapl.com.fudis.data.api.model.*

interface ApiService {

    @FormUrlEncoded
    @POST("user/login")
    suspend fun auth(
        @Field("username") username: String?,
        @Field("password") password: String?
    ): AuthResultApi

    @GET("order/get-list")
    suspend fun orders(
        @Header("Authorization") token: String?,
        @Query("page") page: Int?,
        @Query("per-page") limit: Int?
    ): List<OrderApi>

    @GET("catalog/get-tech-card")
    suspend fun receipt(
        @Header("Authorization") token: String?,
        @Query("id") id: Long?
    ): ReceiptApi

    @GET("catalog/get-menu")
    suspend fun categories(
        @Header("Authorization") token: String?
    ): List<CategoryApi>

    @GET("catalog/get-catalog-items")
    suspend fun catalog(
        @Header("Authorization") token: String?,
        @Query("category_id") id: Long?
    ): List<CatalogApi>

    @GET("catalog/get-products")
    suspend fun products(
        @Header("Authorization") token: String?,
        @Query("organization_id") id: Int?
    ): List<ProductApi>

    @FormUrlEncoded
    @POST("catalog/set-product-stop")
    suspend fun stopProduct(
        @Header("Authorization") token: String?,
        @Field("organization_id") id: Int?,
        @Field("product_id") product: Long?,
        @Field("action") action: Int?
    ): SuccessApi

    @FormUrlEncoded
    @POST("organizations/add-stop")
    suspend fun stopOrganization(
        @Header("Authorization") token: String?,
        @Field("organization_id") id: Int?,
        @Field("drop_time") time: Int?,
        @Field("cause") cause: Int?
    ): SuccessApi

    @FormUrlEncoded
    @POST("organizations/drop-stop")
    suspend fun startOrganization(
        @Header("Authorization") token: String?,
        @Field("organization_id") id: Int?
    ): SuccessApi

}