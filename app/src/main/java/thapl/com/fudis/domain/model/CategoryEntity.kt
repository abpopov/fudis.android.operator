package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryEntity(
    val id: Long,
    val title: String,
    val subCategories: List<CategoryEntity>,
    val children: List<CatalogEntity>,
    var counter: String = ""
) : Parcelable, ListItem {

    override fun unique() = id

    override fun sameContent(other: ListItem) = this == other
}