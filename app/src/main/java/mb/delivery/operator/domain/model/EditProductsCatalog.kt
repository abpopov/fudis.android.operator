package mb.delivery.operator.domain.model

import mb.delivery.operator.data.api.model.EditProductApi
import mb.delivery.operator.data.api.model.EditProductCategoryApi

data class EditProductsCatalog(
    val categories: List<EditProductCategoryApi>,
    val items: List<EditProductApi>
)
