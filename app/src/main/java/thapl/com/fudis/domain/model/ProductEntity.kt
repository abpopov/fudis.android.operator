package thapl.com.fudis.domain.model

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