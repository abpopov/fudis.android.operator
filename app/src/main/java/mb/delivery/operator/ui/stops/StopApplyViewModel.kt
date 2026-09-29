package mb.delivery.operator.ui.stops

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import mb.delivery.operator.domain.case.StopsUseCase
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.OrganizationKitchenIndex
import mb.delivery.operator.domain.model.ProductEntity
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.domain.model.StopListItemEntity
import mb.delivery.operator.domain.model.StopProductCategoryEntity
import mb.delivery.operator.domain.model.StopProductsIndex
import mb.delivery.operator.ui.base.BaseViewModel
import mb.delivery.operator.utils.SingleLiveEvent
import mb.delivery.operator.utils.containsWord

sealed class StopPickRow {
    data class Category(val category: StopProductCategoryEntity) : StopPickRow()
    data class Product(val product: ProductEntity) : StopPickRow()
}

class StopApplyViewModel(private val useCase: StopsUseCase) : BaseViewModel() {

    val organizations = MutableLiveData<ResultEntity<OrganizationKitchenIndex>>()
    val selectedOrg = MutableLiveData<OrganizationKitchenEntity?>()
    val catalog = MutableLiveData<ResultEntity<StopProductsIndex>>()
    val search = MutableLiveData("")
    val selectedCategoryId = MutableLiveData<Long?>(null)
    val applyRequest = SingleLiveEvent<ResultEntity<StopListItemEntity>>()
    val close = SingleLiveEvent<Boolean>()

    val browseRows: LiveData<List<StopPickRow>> = MediatorLiveData<List<StopPickRow>>().apply {
        fun update() {
            val index = (catalog.value as? ResultEntity.Success)?.data
            if (index == null) {
                value = emptyList()
                return
            }
            val query = search.value.orEmpty().trim()
            val categoryId = selectedCategoryId.value
            value = when {
                query.isNotEmpty() -> {
                    index.items
                        .filter { !it.isStopped && it.title.containsWord(query) }
                        .map { StopPickRow.Product(it) }
                }
                categoryId != null -> {
                    val category = index.categories.firstOrNull { it.id == categoryId }
                    category?.items
                        ?.filter { !it.isStopped }
                        ?.map { StopPickRow.Product(it) }
                        .orEmpty()
                }
                else -> {
                    index.categories
                        .filter { it.availableCount > 0 }
                        .map { StopPickRow.Category(it) }
                }
            }
        }
        addSource(catalog) { update() }
        addSource(search) { update() }
        addSource(selectedCategoryId) { update() }
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
            selectedCategoryId.value = null
            catalog.value = ResultEntity.Success(StopProductsIndex(emptyList(), emptyList()))
            return
        }
        useCase.saveOrganizationId(org.id)
        if (!sameOrg) {
            search.value = ""
            selectedCategoryId.value = null
        }
        if (!sameOrg || catalog.value !is ResultEntity.Success) {
            loadProducts(org.id)
        }
    }

    fun openCategory(category: StopProductCategoryEntity) {
        search.value = ""
        selectedCategoryId.value = category.id
    }

    fun backToCategories() {
        selectedCategoryId.value = null
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

    fun isInsideCategory(): Boolean = selectedCategoryId.value != null

    fun categoryTitle(): String? {
        val id = selectedCategoryId.value ?: return null
        return (catalog.value as? ResultEntity.Success)?.data?.categories
            ?.firstOrNull { it.id == id }
            ?.title
    }

    private fun loadProducts(organizationId: Int) {
        selectedCategoryId.value = null
        doRequest(catalog) { useCase.loadProducts(organizationId) }
    }
}
