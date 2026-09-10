package mb.delivery.operator.ui.dialogs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import mb.delivery.operator.R
import mb.delivery.operator.databinding.FragmentPauseBinding
import mb.delivery.operator.domain.model.HIGHLOAD_MODE
import mb.delivery.operator.domain.model.KitchenOption
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.domain.model.STOP_MODE
import mb.delivery.operator.ui.base.BaseDialogFragment
import mb.delivery.operator.ui.orders.OrdersFragment

class PauseFragment : BaseDialogFragment() {

    private val viewModel: PauseViewModel by sharedViewModel()

    private var _binding: FragmentPauseBinding? = null
    private val binding get() = _binding

    private val args: PauseFragmentArgs by navArgs()

    private var orgs: List<OrganizationKitchenEntity> = listOf()
    private var filledMode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPauseBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.tvPause?.isChecked = true
        initListeners()
        initObservers()
        viewModel.load()
    }

    private fun initListeners() {
        binding?.ivClose?.setOnClickListener { close() }
        binding?.tvActionPause?.setOnClickListener { viewModel.submit() }
        binding?.tvOrganization?.setOnClickListener { showOrgPicker() }
        binding?.rgMode?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbModeHighload -> viewModel.selectMode(HIGHLOAD_MODE)
                R.id.rbModeStop -> viewModel.selectMode(STOP_MODE)
            }
        }
        binding?.etComment?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                viewModel.setComment(s?.toString().orEmpty())
            }
        })
    }

    private fun initObservers() {
        viewModel.catalog.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultEntity.Error -> {
                    Toast.makeText(
                        requireContext(),
                        result.error.message.ifEmpty { getString(R.string.pause_load_error) },
                        Toast.LENGTH_SHORT
                    ).show()
                    close()
                }
                is ResultEntity.Success -> bindCatalog(viewModel.idleOrgs())
                else -> Unit
            }
        }
        viewModel.selectedOrg.observe(viewLifecycleOwner) { org ->
            bindSelectedOrg(org)
        }
        viewModel.mode.observe(viewLifecycleOwner) { mode ->
            filledMode = null
            val highload = mode == HIGHLOAD_MODE
            binding?.rgMode?.setOnCheckedChangeListener(null)
            binding?.rbModeHighload?.isChecked = highload
            binding?.rbModeStop?.isChecked = !highload
            binding?.rgMode?.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbModeHighload -> viewModel.selectMode(HIGHLOAD_MODE)
                    R.id.rbModeStop -> viewModel.selectMode(STOP_MODE)
                }
            }
            binding?.vGap?.isVisible = highload
            fillRadios()
        }
        viewModel.cause.observe(viewLifecycleOwner) {
            binding?.etComment?.isVisible = viewModel.needsComment()
        }
        viewModel.canSubmit.observe(viewLifecycleOwner) { enabled ->
            binding?.tvActionPause?.isEnabled = enabled == true
        }
        viewModel.request.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultEntity.Error -> {
                    binding?.tvActionPause?.isEnabled = viewModel.canSubmit.value == true
                    val message = result.error.message
                    if (message.isNotEmpty()) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                is ResultEntity.Loading -> binding?.tvActionPause?.isEnabled = false
                else -> Unit
            }
        }
        viewModel.close.observe(viewLifecycleOwner) { shouldClose ->
            if (shouldClose == true) {
                close()
            }
        }
    }

    private fun bindCatalog(items: List<OrganizationKitchenEntity>) {
        if (items.isEmpty()) {
            Toast.makeText(requireContext(), R.string.highload_no_idle, Toast.LENGTH_SHORT).show()
            close()
            return
        }
        orgs = items
        binding?.tvOrgTitle?.isVisible = true
        binding?.tvOrganization?.isVisible = true
        binding?.rgMode?.isVisible = true
        binding?.vForm?.isVisible = true
        binding?.vActive?.isVisible = false
        bindSelectedOrg(viewModel.selectedOrg.value)
        fillRadios()
    }

    private fun showOrgPicker() {
        if (orgs.isEmpty()) {
            return
        }
        if (orgs.size == 1) {
            viewModel.selectOrganization(orgs.first())
            return
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.pause_org_title)
            .setItems(orgs.map { it.label() }.toTypedArray()) { _, which ->
                orgs.getOrNull(which)?.let { viewModel.selectOrganization(it) }
            }
            .show()
    }

    private fun bindSelectedOrg(org: OrganizationKitchenEntity?) {
        binding?.tvOrganization?.text = org?.label().orEmpty()
        binding?.tvActionPause?.text = getString(
            if (viewModel.mode.value == STOP_MODE) R.string.pause_action_stop else R.string.pause_action_highload
        )
        binding?.vGap?.isVisible = viewModel.mode.value == HIGHLOAD_MODE
        binding?.etComment?.isVisible = viewModel.needsComment()
    }

    private fun fillRadios() {
        val catalog = (viewModel.catalog.value as? ResultEntity.Success)?.data ?: return
        if (binding?.vForm?.isVisible != true) {
            return
        }
        val mode = viewModel.mode.value
        val alreadyFilled = filledMode == mode && (binding?.rgPeriod?.childCount ?: 0) > 0
        if (alreadyFilled) {
            return
        }
        filledMode = mode
        binding?.rgPeriod?.fill(viewModel.timesForMode(), viewModel.dropAfterMinutes.value) {
            viewModel.selectDropAfter(it)
        }
        binding?.rgGap?.fill(catalog.highloadAddTimes, viewModel.highLoadGap.value) {
            viewModel.selectGap(it)
        }
        binding?.rgReason?.fill(catalog.causes, viewModel.cause.value, vertical = true) {
            viewModel.selectCause(it)
        }
        binding?.tvActionPause?.text = getString(
            if (viewModel.mode.value == STOP_MODE) R.string.pause_action_stop else R.string.pause_action_highload
        )
    }

    private fun RadioGroup.fill(
        options: List<KitchenOption>,
        selected: Int?,
        vertical: Boolean = false,
        onSelect: (Int) -> Unit
    ) {
        setOnCheckedChangeListener(null)
        removeAllViews()
        val margin = resources.getDimensionPixelSize(R.dimen.dp28)
        options.forEach { option ->
            val button = layoutInflater.inflate(R.layout.item_pause_radio, this, false) as AppCompatRadioButton
            button.id = View.generateViewId()
            button.text = option.title
            button.tag = option.value
            val params = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                resources.getDimensionPixelSize(R.dimen.dp48)
            )
            if (vertical) {
                params.bottomMargin = resources.getDimensionPixelSize(R.dimen.dp12)
            } else {
                params.marginEnd = margin
            }
            button.layoutParams = params
            addView(button)
            if (selected == option.value) {
                button.isChecked = true
            }
        }
        setOnCheckedChangeListener { group, checkedId ->
            val value = group.findViewById<View>(checkedId)?.tag as? Int ?: return@setOnCheckedChangeListener
            onSelect(value)
        }
    }

    private fun close() {
        setFragmentResult(OrdersFragment.SOURCE_MENU, bundleOf(OrdersFragment.SOURCE_MENU to args.source))
        navigate(PauseFragmentDirections.actionClose())
    }
}
