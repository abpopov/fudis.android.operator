package mb.delivery.operator.data.api

import retrofit2.Call
import retrofit2.http.*
import mb.delivery.operator.data.api.model.*

interface ApiService {

    @POST
    suspend fun auth(
        @Url url: String,
        @Body body: AuthRequestApi
    ): AuthResultApi

    @POST
    fun refreshToken(
        @Url url: String,
        @Body body: RefreshTokenRequestApi
    ): Call<UserApi>

    @GET
    suspend fun orders(
        @Header("Authorization") token: String?,
        @Url url: String
    ): OrdersListApi

    @GET
    suspend fun order(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Query("id") id: Long?
    ): OrderApi

    @POST
    suspend fun changeStatus(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Body body: OrderStatusRequestApi
    ): OrderApi

    @POST
    suspend fun changeItemStatus(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Body body: StatusRequestApi
    ): OrderApi

    @POST
    suspend fun updateOrder(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): OrderApi

    @POST
    suspend fun updateCart(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Body body: OrderCartUpdateRequestApi
    ): OrderApi

    @POST
    suspend fun exportToPos(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Body body: OrderExportToPosRequestApi
    ): OrderExportToPosResponseApi

    @GET
    suspend fun editProducts(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Query("organization_id") id: Int?
    ): EditProductsListApi

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
    suspend fun menu(
        @Header("Authorization") token: String?,
        @Url url: String
    ): MenuApi

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
    ): CatalogProductsListApi

    @GET
    suspend fun stopList(
        @Header("Authorization") token: String?,
        @Url url: String
    ): StopListResponseApi

    @POST
    suspend fun stopProduct(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Body body: SetProductStopRequestApi
    ): StopListItemApi

    @GET
    suspend fun organizations(
        @Header("Authorization") token: String?,
        @Url url: String
    ): OrganizationKitchenListApi

    @POST
    suspend fun setOrganizationStatus(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Body body: OrganizationSetStatusRequestApi
    ): OrganizationKitchenApi

    @POST
    suspend fun dropOrganizationStatus(
        @Header("Authorization") token: String?,
        @Url url: String,
        @Body body: OrganizationDropStatusRequestApi
    ): OrganizationKitchenApi

}