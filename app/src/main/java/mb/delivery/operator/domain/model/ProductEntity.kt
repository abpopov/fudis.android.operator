package mb.delivery.operator.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductEntity(
    val id: Long,
    val title: String,
    var isStopped: Boolean
) : Parcelable, ListItem {

    override fun unique() = id

    override fun sameContent(other: ListItem) = this == other
}

data class StopProductCategoryEntity(
    val id: Long,
    val title: String,
    val items: List<ProductEntity>
) : ListItem {

    val availableCount: Int
        get() = items.count { !it.isStopped }

    override fun unique() = id

    override fun sameContent(other: ListItem) = this == other
}

data class StopProductsIndex(
    val categories: List<StopProductCategoryEntity>,
    val items: List<ProductEntity>
)

data class StopListItemEntity(
    val catalogItemId: Long,
    val title: String,
    val organizationId: Int,
    val organizationTitle: String,
    val isStop: Boolean
) : ListItem {

    override fun unique() = "${organizationId}:${catalogItemId}"

    override fun sameContent(other: ListItem) = this == other
}
