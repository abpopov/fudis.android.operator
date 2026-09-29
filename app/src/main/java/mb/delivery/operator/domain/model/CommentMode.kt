package mb.delivery.operator.domain.model

object CommentMode {
    const val HIDDEN = "hidden"
    const val VIEW = "view"
    const val EDIT = "edit"

    fun normalize(value: String?): String {
        return when (value) {
            HIDDEN, VIEW, EDIT -> value
            else -> VIEW
        }
    }

    fun isVisible(value: String?): Boolean = normalize(value) != HIDDEN

    fun canEdit(value: String?): Boolean = normalize(value) == EDIT
}
