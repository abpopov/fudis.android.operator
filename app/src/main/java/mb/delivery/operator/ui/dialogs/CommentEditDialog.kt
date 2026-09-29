package mb.delivery.operator.ui.dialogs

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import mb.delivery.operator.R

class CommentEditDialog : PanelDialogFragment() {

    override fun bodyLayoutRes() = R.layout.include_dialog_comment
    override fun titleRes() = R.string.order_edit_comment_title

    private var etComment: EditText? = null

    override fun bindBody(root: View) {
        etComment = root.findViewById(R.id.etComment)
        etComment?.setText(arguments?.getString(ARG_COMMENT).orEmpty())
        setPrimaryText(R.string.order_edit_save)
        setSecondaryVisible(true, R.string.order_edit_cancel)
    }

    override fun onPrimaryClick() {
        setFragmentResult(
            REQUEST_KEY,
            bundleOf(RESULT_COMMENT to etComment?.text?.toString().orEmpty())
        )
        dismissAllowingStateLoss()
    }

    companion object {
        const val TAG = "CommentEditDialog"
        const val REQUEST_KEY = "comment_edit_result"
        const val RESULT_COMMENT = "comment"
        private const val ARG_COMMENT = "arg_comment"

        fun show(fm: FragmentManager, comment: String?) {
            CommentEditDialog().apply {
                arguments = bundleOf(ARG_COMMENT to comment.orEmpty())
            }.show(fm, TAG)
        }
    }
}
