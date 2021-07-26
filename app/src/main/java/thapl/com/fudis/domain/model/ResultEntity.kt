package thapl.com.fudis.domain.model

sealed class ResultEntity<out T : Any> {

    object Loading : ResultEntity<Nothing>()
    data class Success<out T : Any>(val data: T) : ResultEntity<T>()
    data class Error(val error: ErrorEntity, val code: Int = 0) : ResultEntity<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$error, code=$code]"
            is Loading -> "Loading"
        }
    }
}