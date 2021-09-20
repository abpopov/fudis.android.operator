package thapl.com.fudis.ui.stops

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import thapl.com.fudis.domain.case.StopsUseCase
import thapl.com.fudis.domain.model.ProductEntity
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.base.BaseViewModel
import thapl.com.fudis.utils.Combined2LiveData
import thapl.com.fudis.utils.SingleLiveEvent
import thapl.com.fudis.utils.containsWord

class StopsViewModel(private val useCase: StopsUseCase) : BaseViewModel() {

    val products = MutableLiveData<ResultEntity<List<ProductEntity>>>()
    val search = MutableLiveData("")
    val filteredProducts: LiveData<List<ProductEntity>> = Transformations.map(search) { s ->
        val productList = (products.value as? ResultEntity.Success)?.data
        productList?.filter { s.isNullOrEmpty() || it.title.containsWord(s) || it.title.replace("_", "").containsWord(s) } ?: listOf()
    }
    val filteredProducts2: LiveData<List<ProductEntity>> = Combined2LiveData(search, products) { s, stops ->
        val productList = (stops as? ResultEntity.Success)?.data
        productList?.filter { s.isNullOrEmpty() || it.title.containsWord(s) || it.title.replace("_", "").containsWord(s) } ?: listOf()
    }

    init {
        getProducts()
    }

    fun refresh() {
        getProducts()
    }

    fun stopItem(id: Long, stop: Boolean) {
        if (stop) {
            val p = products.value
            if (p != null && p is ResultEntity.Success) {
                val itemInList = p.data.firstOrNull { it.id == id }
                itemInList?.let {
                    it.isStopped = stop
                }

                p.let {
                    products.value = it.copy(data = it.data.sortedByDescending { i -> i.isStopped })
                }
            }
        }
        doRequest(products) {
            useCase.stopItem(id, stop).sortedByDescending { it.isStopped }
        }
    }

    private fun getProducts() {
        doRequest(products) {
            useCase.getProducts().sortedByDescending { it.isStopped }
        }
    }
}