package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CatalogEntity(
    val id: Long,
    val title: String,
    val extCode: String?,
    val isActive: Boolean,
    val createdAt: Long?
) : Parcelable, ListItem {

    override fun unique() = id

    override fun sameContent(other: ListItem) = this == other
}