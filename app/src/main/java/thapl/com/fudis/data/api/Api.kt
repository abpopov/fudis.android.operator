package thapl.com.fudis.data.api

import thapl.com.fudis.data.api.model.AuthResultApi

interface Api {
    suspend fun auth(username: String?, password: String?): AuthResultApi
}