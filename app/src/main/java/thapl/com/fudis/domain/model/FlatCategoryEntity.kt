package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class FlatCategoryEntity : ListItem {

    @Parcelize
    data class FlatCategoryRootEntity(
        val id: Long,
        val title: String,
        var expanded: Boolean = false
    )  : Parcelable, FlatCategoryEntity() {

        override fun unique() = id

        override fun sameContent(other: ListItem) = this == other
    }

    @Parcelize
    data class FlatCategorySubEntity(
        val id: Long,
        val parentId: Long,
        val title: String,
        val children: List<FlatCatalogEntity>,
        var counter: String = "",
        var expanded: Boolean = false
    )  : Parcelable, FlatCategoryEntity() {

        override fun unique() = id

        override fun sameContent(other: ListItem) = this == other
    }

    @Parcelize
    data class FlatCatalogEntity(
        val id: Long,
        val title: String,
        var counter: String = ""
    )  : Parcelable, FlatCategoryEntity() {

        override fun unique() = id

        override fun sameContent(other: ListItem) = this == other
    }


}