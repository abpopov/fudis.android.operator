package thapl.com.fudis.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import thapl.com.fudis.R
import thapl.com.fudis.databinding.FragmentHelpBinding
import thapl.com.fudis.ui.base.BaseDialogFragment
import thapl.com.fudis.ui.orders.OrdersFragment.Companion.SOURCE_MENU
import thapl.com.fudis.utils.call

class HelpFragment : BaseDialogFragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding

    private val args: HelpFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    private fun initViews() {
        binding?.tvHelp?.isChecked = true
    }

    private fun initListeners() {
        binding?.ivClose?.setOnClickListener {
            setFragmentResult(SOURCE_MENU, bundleOf(SOURCE_MENU to args.source))
            navigate(HelpFragmentDirections.actionClose())
        }
        binding?.tvPhone?.setOnClickListener {
            context?.call(getString(R.string.help_phone))
        }
    }
}