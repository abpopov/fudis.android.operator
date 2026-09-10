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