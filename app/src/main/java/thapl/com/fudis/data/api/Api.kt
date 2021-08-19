package thapl.com.fudis.data.api

import thapl.com.fudis.data.api.model.*

interface Api {
    suspend fun auth(username: String?, password: String?): AuthResultApi
    suspend fun orders(): List<OrderApi>
    suspend fun changeStatus(order: Long?, status: Int?): StatusApi
    suspend fun categories(): List<CategoryApi>
    suspend fun catalog(id: Long?): List<CatalogApi>
    suspend fun products(id: Int?): List<ProductApi>
    suspend fun stopProduct(id: Int?, product: Long?, stop: Boolean?): SuccessApi
    suspend fun receipt(id: Long?): ReceiptApi
    suspend fun stopOrganization(id: Int?, time: Int?, cause: Int?): SuccessApi
    suspend fun startOrganization(id: Int?): SuccessApi
}