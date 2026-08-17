package mb.delivery.operator.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import mb.delivery.operator.databinding.FragmentStopConfirmBinding
import mb.delivery.operator.ui.base.BaseDialogFragment
import mb.delivery.operator.ui.stops.StopsFragment.Companion.GOOD_ID

class StopConfirmFragment : BaseDialogFragment() {

    private var _binding: FragmentStopConfirmBinding? = null
    private val binding get() = _binding

    private val args: StopConfirmFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStopConfirmBinding.inflate(inflater, container, false)
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
        binding?.tvStops?.isChecked = true
    }

    private fun initListeners() {
        binding?.ivClose?.setOnClickListener {
            navigate(StopConfirmFragmentDirections.actionClose())
        }
        binding?.tvYes?.setOnClickListener {
            setFragmentResult(GOOD_ID, bundleOf(GOOD_ID to args.id))
            navigate(StopConfirmFragmentDirections.actionClose())
        }
        binding?.tvNo?.setOnClickListener {
            navigate(StopConfirmFragmentDirections.actionClose())
        }
    }
}