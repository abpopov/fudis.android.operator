package mb.delivery.operator.data.auth

import mb.delivery.operator.data.api.model.UserApi
import mb.delivery.operator.data.prefs.Prefs
import mb.delivery.operator.notifications.OperatorWebSocket

object AuthSession {

    fun applyUser(
        prefs: Prefs,
        socket: OperatorWebSocket,
        events: SessionEvents,
        user: UserApi
    ): Boolean {
        val token = user.accessToken
        if (token.isNullOrEmpty()) {
            return false
        }
        prefs.setUserToken(token)
        prefs.setRefreshToken(user.refreshToken)
        prefs.setOrganizationId(user.organizations?.firstOrNull()?.id)
        prefs.setWsSocketUrl(user.wsSocketUrl)
        prefs.setWsSocketChannel(user.wsSocketChannel)
        prefs.setWsSocketToken(user.wsSocketToken)
        events.reset()
        val wsUrl = user.wsSocketUrl
        val wsToken = user.wsSocketToken
        val wsChannel = user.wsSocketChannel
        if (!wsUrl.isNullOrEmpty() && !wsToken.isNullOrEmpty() && !wsChannel.isNullOrEmpty()) {
            socket.init(wsUrl, wsToken, wsChannel)
        }
        return true
    }

    fun expire(prefs: Prefs, socket: OperatorWebSocket, events: SessionEvents) {
        socket.clear()
        prefs.clearSession()
        events.notifyExpired()
    }
}
