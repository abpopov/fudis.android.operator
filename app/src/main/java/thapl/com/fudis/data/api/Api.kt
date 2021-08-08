package thapl.com.fudis.data.api

import thapl.com.fudis.data.api.model.AuthResultApi
import thapl.com.fudis.data.api.model.OrderApi

interface Api {
    suspend fun auth(username: String?, password: String?): AuthResultApi
    suspend fun orders(): List<OrderApi>
}