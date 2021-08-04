package thapl.com.fudis.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import thapl.com.fudis.domain.case.RegisterUseCase
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.base.BaseViewModel
import thapl.com.fudis.utils.SingleLiveEvent

class RegisterViewModel(private val useCase: RegisterUseCase) : BaseViewModel() {

    private val _authValidate = MutableLiveData(false)

    val authResult = SingleLiveEvent<ResultEntity<Any>>()
    val authValidate = Transformations.distinctUntilChanged(_authValidate)

    fun auth(login: String?, pwd: String?) {
        if (login.isNullOrEmpty()) return
        if (pwd.isNullOrEmpty()) return
        doRequest(authResult) {
            useCase.login(login, pwd)
        }
    }

    fun setValidate(value: Boolean) {
        _authValidate.postValue(value)
    }
}