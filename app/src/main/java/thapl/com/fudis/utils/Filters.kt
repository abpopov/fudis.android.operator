package thapl.com.fudis.utils

fun String.containsWord(word: String): Boolean {
    return try {
        val words = this.split(" ")
        var result = false
        words.forEach {
            when {
                it.trim().startsWith(word, true) -> {
                    result = true
                }
                it.trim().startsWith("\"$word", true) -> {
                    result = true
                }
                it.trim().startsWith("'$word", true) -> {
                    result = true
                }
            }
        }
        result
    } catch (e: Exception) {
        false
    }
}