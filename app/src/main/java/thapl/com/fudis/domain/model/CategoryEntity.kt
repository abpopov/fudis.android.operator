package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryEntity(
    val id: Long,
    val title: String,
    val isActive: Boolean,
    val onMainPage: Boolean,
    val onTopMenu: Boolean,
    val children: List<CategoryEntity>
) : Parcelable, ListItem {

    override fun unique() = id

    override fun sameContent(other: ListItem) = this == other
}