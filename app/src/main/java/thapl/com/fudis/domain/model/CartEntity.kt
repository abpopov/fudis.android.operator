package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartEntity(
    val item: CatalogItemEntity,
    val modifiers: List<ModifierEntity>,
    val count: Int
) : Parcelable, ListItem {

    override fun unique() = item.id

    override fun sameContent(other: ListItem) = this == other
}