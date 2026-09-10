package mb.delivery.operator.notifications.socket

import org.json.JSONObject

class Parser {

    enum class ParseResult {
        IS_AUTHENTICATED, PUBLISH, REMOVE_TOKEN, SET_TOKEN, EVENT, ACK_RECEIVE
    }

    companion object {
        fun parse(data: Any?, event: String?): ParseResult {
            return if (data is JSONObject && data.opt("isAuthenticated") != null) {
                ParseResult.IS_AUTHENTICATED
            } else if (event != null) {
                when (event) {
                    "#publish" -> {
                        ParseResult.PUBLISH
                    }
                    "#removeAuthToken" -> {
                        ParseResult.REMOVE_TOKEN
                    }
                    "#setAuthToken" -> {
                        ParseResult.SET_TOKEN
                    }
                    else -> {
                        ParseResult.EVENT
                    }
                }
            } else {
                ParseResult.ACK_RECEIVE
            }
        }
    }

}