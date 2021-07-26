package thapl.com.fudis.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.HttpException
import thapl.com.fudis.data.api.model.ErrorApi
import thapl.com.fudis.domain.mapper.ErrorApiToEntityMapper
import thapl.com.fudis.domain.model.ErrorEntity
import thapl.com.fudis.domain.model.ResultEntity
import java.io.IOException
import java.net.UnknownHostException

abstract class BaseViewModel : ViewModel() {

    inline fun <T : Any> doRequest(
        liveData: MutableLiveData<ResultEntity<T>>,
        needLoading: Boolean = true,
        crossinline block: suspend () -> T
    ): Job {
        return viewModelScope.launch {
            if (needLoading) liveData.postValue(ResultEntity.Loading)
            liveData.postValue(safeExecute(block))
        }
    }

    inline fun <T : Any> doGlobalRequest(
        liveData: MutableLiveData<ResultEntity<T>>,
        needLoading: Boolean = true,
        crossinline block: suspend () -> T
    ): Job {
        return GlobalScope.launch {
            if (needLoading) liveData.postValue(ResultEntity.Loading)
            liveData.postValue(safeExecute(block))
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend inline fun <T : Any> safeExecute(crossinline block: suspend () -> T): ResultEntity<T> {
        return try {
            val r: T = withContext(Dispatchers.IO) { block() }
            ResultEntity.Success(r)
        } catch (ex: IOException) {
            ResultEntity.Error(ErrorEntity(0, "io", "IOException", ex.message ?: ""), 999)
        } catch (ex: UnknownHostException) {
            ResultEntity.Error(ErrorEntity(0, "host", "UnknownHostException", ex.message ?: ""), 998)
        } catch (ex: HttpException) {
            val error = try {
                val json = JSONObject(ex.response()?.errorBody()?.string() ?: "")
                val e = json.getJSONObject("error")
                ErrorApi(
                    code = e.optInt("code"),
                    textCode = e.optString("textCode"),
                    type = e.optString("type"),
                    message = e.optString("message")
                )
            } catch (e: Exception) {
                ErrorApi()
            }
            ResultEntity.Error(ErrorApiToEntityMapper.map(error), ex.code())
        } catch (ex: Exception) {
            ResultEntity.Error(ErrorEntity(0, "other", "Exception", ex.message ?: ""), 900)
        }
    }
}