package mb.delivery.operator.ui.stops

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mb.delivery.operator.domain.case.StopsUseCase
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.OrganizationKitchenIndex
import mb.delivery.operator.domain.model.ProductEntity
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.domain.model.StopListItemEntity
import mb.delivery.operator.ui.base.BaseViewModel
import mb.delivery.operator.utils.Combined2LiveData
import mb.delivery.operator.utils.SingleLiveEvent
import mb.delivery.operator.utils.containsWord

class StopApplyViewModel(private val useCase: StopsUseCase) : BaseViewModel() {

    val organizations = MutableLiveData<ResultEntity<OrganizationKitchenIndex>>()
    val selectedOrg = MutableLiveData<OrganizationKitchenEntity?>()
    val products = MutableLiveData<ResultEntity<List<ProductEntity>>>()
    val search = MutableLiveData("")
    val applyRequest = SingleLiveEvent<ResultEntity<StopListItemEntity>>()
    val close = SingleLiveEvent<Boolean>()

    val filteredProducts: LiveData<List<ProductEntity>> = Combined2LiveData(search, products) { query, result ->
        val list = (result as? ResultEntity.Success)?.data.orEmpty().filter { !it.isStopped }
        val q = query.orEmpty()
        list.filter { q.isEmpty() || it.title.containsWord(q) }
    }

    fun load() {
        doPostActionRequest(
            organizations,
            block = { useCase.loadOrganizations() },
            action = { result ->
                if (result is ResultEntity.Success) {
                    val orgs = result.data.items
                    val saved = useCase.getSavedOrganizationId()
                    val selected = when {
                        orgs.size == 1 -> orgs.first()
                        else -> orgs.firstOrNull { it.id == saved }
                    }
                    selectedOrg.value = selected
                    selected?.let {
                        useCase.saveOrganizationId(it.id)
                        loadProducts(it.id)
                    }
                }
            }
        )
    }

    fun selectOrganization(org: OrganizationKitchenEntity?) {
        val sameOrg = selectedOrg.value?.id == org?.id
        selectedOrg.value = org
        if (org == null) {
            search.value = ""
            products.value = ResultEntity.Success(listOf())
            return
        }
        useCase.saveOrganizationId(org.id)
        if (!sameOrg) {
            search.value = ""
        }
        if (!sameOrg || products.value !is ResultEntity.Success) {
            loadProducts(org.id)
        }
    }

    fun apply(item: ProductEntity) {
        val org = selectedOrg.value ?: return
        doPostActionRequest(
            applyRequest,
            block = { useCase.setProductStop(org.id, item.id, true) },
            action = { result ->
                if (result is ResultEntity.Success) {
                    close.postValue(true)
                }
            }
        )
    }

    private fun loadProducts(organizationId: Int) {
        doRequest(products) { useCase.loadProducts(organizationId) }
    }
}
