package mb.delivery.operator.data.api

object ProjectUrlBuilder {

    private const val ADM_HOST_SUFFIX = ".adm.thapl.com"

    fun fromProjectCode(input: String): String {
        val digits = input.filter { it.isDigit() }
        require(digits.length >= 2) { "Project code must have at least 2 digits" }
        return "https://project$digits$ADM_HOST_SUFFIX"
    }

    fun fromCustomHost(input: String): String = normalizeHostUrl(input)

    fun operatorApiUrl(baseUrl: String): String {
        return "${baseUrl.trimEnd('/')}/api/operator"
    }

    fun normalizeHostUrl(raw: String): String {
        var url = raw.trim()
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://$url"
        }
        while (url.endsWith("/")) {
            url = url.dropLast(1)
        }
        require(url.length > 8) { "Invalid host URL" }
        return url
    }

    fun isLikelyUrl(input: String): Boolean {
        val value = input.trim()
        return value.startsWith("http://") ||
            value.startsWith("https://") ||
            value.contains('.')
    }
}
