package mb.delivery.operator.ui.highload

import androidx.lifecycle.MutableLiveData
import mb.delivery.operator.domain.case.PauseUseCase
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.OrganizationKitchenIndex
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.ui.base.BaseViewModel
import mb.delivery.operator.utils.SingleLiveEvent

class HighloadViewModel(private val useCase: PauseUseCase) : BaseViewModel() {

    val catalog = MutableLiveData<ResultEntity<OrganizationKitchenIndex>>()
    val dropRequest = SingleLiveEvent<ResultEntity<OrganizationKitchenEntity>>()

    fun load() {
        doRequest(catalog) { useCase.load() }
    }

    fun drop(organizationId: Int) {
        doPostActionRequest(
            dropRequest,
            block = { useCase.dropStatus(organizationId) },
            action = { result ->
                if (result is ResultEntity.Success) {
                    load()
                }
            }
        )
    }

    fun activeOrgs(): List<OrganizationKitchenEntity> {
        return (catalog.value as? ResultEntity.Success)?.data?.items
            ?.filter { it.isActive }
            .orEmpty()
    }

    fun hasIdleOrgs(): Boolean {
        return (catalog.value as? ResultEntity.Success)?.data?.items
            ?.any { !it.isActive } == true
    }
}
