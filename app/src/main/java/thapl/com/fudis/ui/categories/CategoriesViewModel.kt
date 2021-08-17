package thapl.com.fudis.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import thapl.com.fudis.domain.case.CategoriesUseCase
import thapl.com.fudis.domain.model.CategoryEntity
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.base.BaseViewModel
import thapl.com.fudis.utils.containsWord

class CategoriesViewModel(private val useCase: CategoriesUseCase) : BaseViewModel() {

    val categories = MutableLiveData<ResultEntity<List<CategoryEntity>>>()
    val search = MutableLiveData("")
    val filteredCategories: LiveData<List<CategoryEntity>> = Transformations.map(search) { s ->
        val productList = (categories.value as? ResultEntity.Success)?.data
        productList?.filter { ce ->
            s.isNullOrEmpty() || ce.title.containsWord(s) || ce.subCategories.any {
                it.title.containsWord(s)
            } || ce.children.any {
                it.title.containsWord(s)
            }
        } ?: listOf()
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