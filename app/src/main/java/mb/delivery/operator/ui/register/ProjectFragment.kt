package mb.delivery.operator.ui.register

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.viewModel
import mb.delivery.operator.R
import mb.delivery.operator.databinding.FragmentProjectBinding
import mb.delivery.operator.ui.base.BaseFragment
import mb.delivery.operator.utils.EmailValidator
import mb.delivery.operator.utils.Validate
import mb.delivery.operator.utils.ViewsValidator
import mb.delivery.operator.utils.hideKeyboard

class ProjectFragment : BaseFragment() {

    private val model: RegisterViewModel by viewModel()

    private var _binding: FragmentProjectBinding? = null
    private val binding get() = _binding

    private var validator: Validate? = null
    private var hostMode = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProjectBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        initObservers()
        applyMode()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        validator = ViewsValidator(
            listOfNotNull(
                EmailValidator(
                    view = binding?.etProject
                )
            )
        ) {
            model.setValidate(it)
        }
        binding?.etProject?.setText("")
    }

    private fun initListeners() {
        binding?.tvToggle?.setOnClickListener {
            hostMode = !hostMode
            binding?.etProject?.setText("")
            applyMode()
        }
        binding?.tvNext?.setOnClickListener {
            if (validator?.completeValidate() != true) {
                return@setOnClickListener
            }
            val value = binding?.etProject?.text?.toString()?.trim().orEmpty()
            try {
                if (hostMode) {
                    model.setCustomHost(value)
                } else {
                    model.setProjectCode(value)
                }
                context?.hideKeyboard(binding?.etProject)
                navigate(ProjectFragmentDirections.actionAuth())
            } catch (_: IllegalArgumentException) {
            }
        }
    }

    private fun initObservers() {
        model.authValidate.observe(viewLifecycleOwner) { enable ->
            binding?.tvNext?.isEnabled = enable
        }
    }

    private fun applyMode() {
        val input = binding?.etProject ?: return
        if (hostMode) {
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            input.filters = arrayOf()
            input.hint = getString(R.string.auth_host_hint)
            binding?.tvNext?.text = getString(R.string.auth_host_next)
            binding?.tvToggle?.text = getString(R.string.auth_host_or_project)
        } else {
            input.inputType = InputType.TYPE_CLASS_NUMBER
            input.filters = arrayOf(InputFilter.LengthFilter(6))
            input.hint = getString(R.string.auth_project_hint)
            binding?.tvNext?.text = getString(R.string.auth_project_next)
            binding?.tvToggle?.text = getString(R.string.auth_project_or_host)
        }
    }
}
