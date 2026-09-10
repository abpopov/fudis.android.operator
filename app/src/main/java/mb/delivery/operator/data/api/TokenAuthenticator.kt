package mb.delivery.operator.data.api

import mb.delivery.operator.data.api.model.RefreshTokenRequestApi
import mb.delivery.operator.data.auth.AuthSession
import mb.delivery.operator.data.auth.SessionEvents
import mb.delivery.operator.data.prefs.Prefs
import mb.delivery.operator.notifications.OperatorWebSocket
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val prefs: Prefs,
    private val sessionEvents: SessionEvents,
    private val socket: OperatorWebSocket,
    private val refreshService: ApiService,
    private val refreshUrl: () -> String
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        val url = response.request.url.toString()
        if (url.contains("/user/login") || url.contains("/user/refresh-token")) {
            return null
        }
        if (responseCount(response) >= 2) {
            return null
        }

        synchronized(lock) {
            val currentToken = prefs.getUserToken()
            val requestToken = bearerToken(response.request)
            if (!currentToken.isNullOrEmpty() && currentToken != requestToken) {
                return authorized(response.request, currentToken)
            }

            val refresh = prefs.getRefreshToken()
            if (refresh.isNullOrEmpty()) {
                AuthSession.expire(prefs, socket, sessionEvents)
                return null
            }

            return try {
                val refreshResponse = refreshService.refreshToken(
                    refreshUrl(),
                    RefreshTokenRequestApi(refresh)
                ).execute()
                val user = refreshResponse.body()
                val access = user?.accessToken
                if (!refreshResponse.isSuccessful || user == null || access.isNullOrEmpty()) {
                    if (refreshResponse.code() == 401 || refreshResponse.code() == 400) {
                        AuthSession.expire(prefs, socket, sessionEvents)
                    }
                    return null
                }
                AuthSession.applyUser(prefs, socket, sessionEvents, user)
                authorized(response.request, access)
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun authorized(request: Request, token: String): Request {
        return request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
    }

    private fun bearerToken(request: Request): String? {
        val header = request.header("Authorization") ?: return null
        return header.removePrefix("Bearer").trim()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
