package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartEntity(
    val item: CatalogItemEntity,
    val modifiers: List<ModifierEntity>,
    val id: Int,
    val count: Int,
    val status: Int,
    val hasTechCard: Boolean
) : Parcelable, ListItem {

    override fun unique() = item.id

    override fun sameContent(other: ListItem) = this == other && status == (other as? CartEntity)?.status

    companion object {
        const val STATUS_NEW = 0
        const val STATUS_READY_COOK = 3
        const val STATUS_COOKING = 5
        const val STATUS_COOKED = 7
        const val STATUS_DONE = 10
    }
}