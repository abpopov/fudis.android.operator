package thapl.com.fudis.domain.model

interface ListItem {
    fun unique(): Any
    fun sameContent(other: ListItem): Boolean
}