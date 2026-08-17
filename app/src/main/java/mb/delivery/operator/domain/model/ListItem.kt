package mb.delivery.operator.domain.model

interface ListItem {
    fun unique(): Any
    fun sameContent(other: ListItem): Boolean
}