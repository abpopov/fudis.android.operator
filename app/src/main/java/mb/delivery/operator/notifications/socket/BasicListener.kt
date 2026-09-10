package mb.delivery.operator.notifications.socket

import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFrame

interface BasicListener {
    fun onConnected(socket: Socket, headers: Map<String, List<String>>)
    fun onDisconnected(
        socket: Socket?,
        serverCloseFrame: WebSocketFrame?,
        clientCloseFrame: WebSocketFrame?,
        closedByServer: Boolean
    )
    fun onConnectError(socket: Socket?, exception: WebSocketException?)
    fun onAuthentication(socket: Socket?, status: Boolean?)
    fun onSetAuthToken(token: String?, socket: Socket?)
}