package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CatalogEntity(
    val id: Long,
    val title: String,
    var counter: String = "",
    var isRoot: Boolean = false
) : Parcelable, ListItem {

    override fun unique() = id

    override fun sameContent(other: ListItem) = this == other
}