package thapl.com.fudis.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import thapl.com.fudis.domain.case.CategoriesUseCase
import thapl.com.fudis.domain.model.CatalogEntity
import thapl.com.fudis.domain.model.CategoryEntity
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.base.BaseViewModel
import thapl.com.fudis.utils.containsWord

class CategoriesViewModel(private val useCase: CategoriesUseCase) : BaseViewModel() {

    val categories = MutableLiveData<ResultEntity<List<CategoryEntity>>>()
    val search = MutableLiveData("")
    val filteredCategories: LiveData<Pair<List<CatalogEntity>, List<CategoryEntity>>> =
        Transformations.map(search) { s ->
            val productList = (categories.value as? ResultEntity.Success)?.data
            if (s.isNullOrEmpty()) {
                Pair(listOf(), productList ?: listOf())
            } else {
                val items = mutableListOf<CatalogEntity>()
                productList?.forEach { ce ->
                    items.addAll(ce.children)
                    ce.subCategories.forEach {
                        items.addAll(it.children)
                    }
                }
                Pair(items.filter { ce ->
                    ce.title.containsWord(s)
                }, listOf())
            }
        }

    init {
        getCategories()
    }

    fun refresh() {
        getCategories()
    }

    private fun getCategories() {
        doRequest(categories) {
            useCase.getCategories()
        }
    }
}