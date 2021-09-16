package thapl.com.fudis.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class Combined2LiveData<T1, T2, R>(
    t1LiveData: LiveData<T1>,
    t2LiveData: LiveData<T2>,
    private val combine: (T1?, T2?) -> R
) : MediatorLiveData<R>() {
    private var t1: T1? = null
    private var t2: T2? = null

    init {
        addSource(t1LiveData) { t: T1 ->
            t1 = t
            notifyOnChange()
        }
        addSource(t2LiveData) { t: T2 ->
            t2 = t
            notifyOnChange()
        }
    }

    private fun notifyOnChange() {
        try {
            postValue(combine.invoke(t1, t2))
        } catch (e: IllegalArgumentException) {

        }
    }
}