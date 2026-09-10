package mb.delivery.operator.ui.stops

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import mb.delivery.operator.domain.case.StopsUseCase
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.OrganizationKitchenIndex
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.domain.model.StopListItemEntity
import mb.delivery.operator.ui.base.BaseViewModel
import mb.delivery.operator.utils.SingleLiveEvent
import mb.delivery.operator.utils.containsWord

class StopsViewModel(private val useCase: StopsUseCase) : BaseViewModel() {

    val organizations = MutableLiveData<ResultEntity<OrganizationKitchenIndex>>()
    val selectedOrg = MutableLiveData<OrganizationKitchenEntity?>()
    val items = MutableLiveData<ResultEntity<List<StopListItemEntity>>>()
    val search = MutableLiveData("")
    val dropRequest = SingleLiveEvent<ResultEntity<StopListItemEntity>>()

    val visibleItems: LiveData<List<StopListItemEntity>> = MediatorLiveData<List<StopListItemEntity>>().apply {
        fun update() {
            val orgId = selectedOrg.value?.id
            val list = (items.value as? ResultEntity.Success)?.data.orEmpty()
                .filter { orgId == null || it.organizationId == orgId }
            val query = search.value.orEmpty()
            value = list.filter {
                query.isEmpty() || it.title.containsWord(query) || it.organizationTitle.containsWord(query)
            }
        }
        addSource(items) { update() }
        addSource(search) { update() }
        addSource(selectedOrg) { update() }
    }

    fun load() {
        loadOrganizations()
        loadStopList()
    }

    fun loadStopList() {
        doRequest(items) { useCase.loadStopList() }
    }

    fun selectOrganization(org: OrganizationKitchenEntity?) {
        if (selectedOrg.value?.id == org?.id) {
            return
        }
        selectedOrg.value = org
        org?.let { useCase.saveOrganizationId(it.id) }
    }

    fun drop(item: StopListItemEntity) {
        doPostActionRequest(
            dropRequest,
            block = { useCase.setProductStop(item.organizationId, item.catalogItemId, false) },
            action = { result ->
                if (result is ResultEntity.Success) {
                    loadStopList()
                }
            }
        )
    }

    fun hasManyOrganizations(): Boolean {
        return organizationsList().size > 1
    }

    fun organizationsList(): List<OrganizationKitchenEntity> {
        return (organizations.value as? ResultEntity.Success)?.data?.items.orEmpty()
    }

    private fun loadOrganizations() {
        doPostActionRequest(
            organizations,
            needLoading = false,
            block = { useCase.loadOrganizations() },
            action = { result ->
                if (result is ResultEntity.Success) {
                    val orgs = result.data.items
                    val saved = useCase.getSavedOrganizationId()
                    val selected = when {
                        orgs.size == 1 -> orgs.first()
                        else -> orgs.firstOrNull { it.id == saved } ?: orgs.firstOrNull()
                    }
                    selectedOrg.value = selected
                    selected?.let { useCase.saveOrganizationId(it.id) }
                }
            }
        )
    }
}
