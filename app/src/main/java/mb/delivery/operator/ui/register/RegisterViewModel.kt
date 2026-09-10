package mb.delivery.operator.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import mb.delivery.operator.domain.case.RegisterUseCase
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.ui.base.BaseViewModel
import mb.delivery.operator.utils.SingleLiveEvent

class RegisterViewModel(private val useCase: RegisterUseCase) : BaseViewModel() {

    private val _authValidate = MutableLiveData(false)

    val authResult = SingleLiveEvent<ResultEntity<Any>>()
    val authValidate = _authValidate.distinctUntilChanged()

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

    fun setProjectCode(project: String) {
        useCase.setProjectCode(project)
    }

    fun setCustomHost(host: String) {
        useCase.setCustomHost(host)
    }

    fun clearHost() {
        useCase.clearHostConfig()
    }

    fun getHostLabel() = useCase.getHostDisplayLabel()
}
