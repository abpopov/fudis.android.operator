package thapl.com.fudis.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import thapl.com.fudis.domain.case.CategoriesUseCase
import thapl.com.fudis.domain.model.CatalogEntity
import thapl.com.fudis.domain.model.CategoryEntity
import thapl.com.fudis.domain.model.FlatCategoryEntity
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.base.BaseViewModel
import thapl.com.fudis.utils.Combined2LiveData
import thapl.com.fudis.utils.SingleLiveEvent
import thapl.com.fudis.utils.containsWord

class CategoriesViewModel(private val useCase: CategoriesUseCase) : BaseViewModel() {

    private val categories = MutableLiveData<MutableList<FlatCategoryEntity>?>()
    val categoriesResult = SingleLiveEvent<ResultEntity<List<CategoryEntity>>>()
    val search = MutableLiveData("")

    val filteredCategories: LiveData<Pair<List<CatalogEntity>, List<FlatCategoryEntity>>> =
        Combined2LiveData(search, categories) { s, cats ->
            val productList = (categoriesResult.value as? ResultEntity.Success)?.data
            if (s.isNullOrEmpty()) {
                Pair(listOf(), cats ?: listOf())
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
        doRequest(categoriesResult) {
            useCase.getCategories()
        }
    }

    fun handleCategoryLoad(cats: List<CategoryEntity>?) {
        categories.postValue(cats?.map { c1 ->
            FlatCategoryEntity.FlatCategoryRootEntity(
                id = c1.id,
                title = c1.title
            )
        }?.toMutableList() ?: mutableListOf())
    }

    fun handleCategoryClick(position: Int, item: FlatCategoryEntity.FlatCategoryRootEntity) {
        val c = categories.value
        if (c != null) {
            val parent = (categoriesResult.value as? ResultEntity.Success)?.data?.firstOrNull {
                it.id == item.id
            } ?: return
            var start = 0
            var next = 0
            var add = true
            val itemSubcategories =
                parent.subCategories.mapIndexed { i, it ->
                    FlatCategoryEntity.FlatCategorySubEntity(
                        id = it.id,
                        parentId = parent.id,
                        title = it.title,
                        children = it.children.map {
                            FlatCategoryEntity.FlatCatalogEntity(
                                id = it.id,
                                title = it.title
                            )
                        },
                        counter = "${i + 1}"
                    )
                }
            val itemChildren =
                parent.children.mapIndexed { i, it ->
                    FlatCategoryEntity.FlatCatalogEntity(
                        id = it.id,
                        title = it.title,
                        counter = (itemSubcategories.mapNotNull { categoryEntity ->
                            if (categoryEntity.children.isEmpty()) null else categoryEntity
                        }.size + i + 1).toString()
                    )
                }
            c.onEachIndexed { i, it ->
                if (it is FlatCategoryEntity.FlatCategoryRootEntity && it.id == item.id) {
                    start = i
                } else if (it is FlatCategoryEntity.FlatCategoryRootEntity) {
                    if (i > start && next == 0) {
                        next = i
                    }
                }
            }
            c.getOrNull(start + 1)?.let {
                if (itemSubcategories.isNotEmpty()) {
                    if (itemSubcategories.getOrNull(0) == it) {
                        add = false
                    }
                } else if (itemChildren.isNotEmpty()) {
                    if (itemChildren.getOrNull(0) == it) {
                        add = false
                    }
                }
            }
            if (add) {
                c.getOrNull(start)?.let {
                    (it as? FlatCategoryEntity.FlatCategoryRootEntity)?.expanded = true
                }
                if (itemSubcategories.isNotEmpty()) {
                    c.addAll(start + 1, itemSubcategories)
                }
                if (itemChildren.isNotEmpty()) {
                    c.addAll(start + 1 + (itemSubcategories.size), itemChildren)
                }
            } else {
                c.getOrNull(start)?.let {
                    (it as? FlatCategoryEntity.FlatCategoryRootEntity)?.expanded = false
                }
                if (next <= start) {
                    next = c.size
                }
                var stop = false
                for (i in 1 until next - start) {
                    try {
                        if (stop.not()) {
                            if (c.getOrNull(start + 1) is FlatCategoryEntity.FlatCategoryRootEntity) {
                                stop = true
                            } else {
                                c.removeAt(start + 1)
                            }
                        }
                    } catch (e: Exception) {

                    }
                }
            }
            categories.postValue(c)
        }
    }

    fun handleCategorySubClick(position: Int, item: FlatCategoryEntity.FlatCategorySubEntity) {
        val c = categories.value
        if (c != null) {
            val parent = (categoriesResult.value as? ResultEntity.Success)?.data?.firstOrNull {
                it.id == item.parentId
            } ?: return
            val child = parent.subCategories.firstOrNull {
                it.id == item.id
            } ?: return
            val parentPosition = parent.subCategories.indexOf(child)
            var start = 0
            var add = true
            val itemChildren =
                child.children.mapIndexed { i, it ->
                    FlatCategoryEntity.FlatCatalogEntity(
                        id = it.id,
                        title = it.title,
                        counter = "${parentPosition + 1}.${i + 1}"
                    )
                }
            c.onEachIndexed { i, it ->
                if (it is FlatCategoryEntity.FlatCategorySubEntity && it.id == item.id) {
                    start = i
                }
            }
            c.getOrNull(start + 1)?.let {
                if (itemChildren.isNotEmpty()) {
                    if (itemChildren.getOrNull(0) == it) {
                        add = false
                    }
                }
            }
            if (add) {
                c.getOrNull(start)?.let {
                    (it as? FlatCategoryEntity.FlatCategorySubEntity)?.expanded = true
                }
                if (itemChildren.isNotEmpty()) {
                    c.addAll(start + 1, itemChildren)
                }
            } else {
                c.getOrNull(start)?.let {
                    (it as? FlatCategoryEntity.FlatCategoryRootEntity)?.expanded = false
                }
                for (i in 1..itemChildren.size) {
                    try {
                        c.removeAt(start + 1)
                    } catch (e: Exception) {

                    }
                }
            }
            categories.postValue(c)
        }
    }
}