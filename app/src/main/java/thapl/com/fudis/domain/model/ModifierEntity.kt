package thapl.com.fudis.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ModifierEntity(
    val count: Int,
    val modificator: ModificatorEntity
) : Parcelable, ListItem {

    override fun unique() = modificator.id

    override fun sameContent(other: ListItem) = this == other
}