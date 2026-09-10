package mb.delivery.operator.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.viewModel
import mb.delivery.operator.R
import mb.delivery.operator.data.api.model.BAD_LOGIN
import mb.delivery.operator.databinding.FragmentAuthBinding
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.ui.base.BaseFragment
import mb.delivery.operator.utils.*

class RegisterFragment : BaseFragment() {

    private val model: RegisterViewModel by viewModel()

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding

    private var validator: Validate? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        validator = ViewsValidator(
            listOfNotNull(
                EmailValidator(
                    view = binding?.etLogin
                ),
                PasswordValidator(
                    view = binding?.etPwd
                )
            )
        ) {
            model.setValidate(it)
        }
        binding?.tvProjectValue?.text = model.getHostLabel()
        binding?.tvProjectLabel?.text = if (model.getHostLabel().startsWith("http")) {
            getString(R.string.auth_host_label)
        } else {
            getString(R.string.auth_project_label)
        }
    }

    private fun initListeners() {
        binding?.vRoot?.setOnClickListener {
            binding?.vRoot?.transitionToStart()
        }
        binding?.tvProjectNext?.setOnClickListener {
            model.clearHost()
            navigate(RegisterFragmentDirections.actionProject())
        }
        binding?.tvNext?.setOnClickListener {
            if (validator?.completeValidate() == true) {
                context?.hideKeyboard(binding?.etLogin, binding?.etPwd)
                if (debug()) {
                    model.auth(
                        "ContentM",
                        "dsfdsskenerJDD825MdskjdsdNdfk-34#@jsdlKkdasMljsd"
                    )
                } else {
                    model.auth(
                        binding?.etLogin?.text?.toString()?.trim(),
                        binding?.etPwd?.text?.toString()?.trim()
                    )
                }
            }
        }
    }

    private fun initObservers() {
        model.authValidate.observe(viewLifecycleOwner) { enable ->
            binding?.tvNext?.isEnabled = enable
        }
        model.authResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultEntity.Loading -> {
                    binding?.vRoot?.transitionToStart()
                    binding?.etLogin?.isEnabled = false
                    binding?.etPwd?.isEnabled = false
                    binding?.tvNext?.isEnabled = false
                }

                is ResultEntity.Success -> {
                    binding?.vRoot?.transitionToStart()
                    binding?.etLogin?.isEnabled = false
                    binding?.etPwd?.isEnabled = false
                    binding?.tvNext?.isEnabled = false
                    navigate(RegisterFragmentDirections.actionOrders())
                }

                is ResultEntity.Error -> {
                    if (result.error.code == BAD_LOGIN) {
                        binding?.tvError?.text = getString(R.string.auth_error)
                        binding?.vRoot?.transitionToEnd()
                    } else {
                        result.error.message.takeIf { it.isNotEmpty() }?.let {
                            binding?.tvError?.text = it
                            binding?.vRoot?.transitionToEnd()
                        }
                    }
                    binding?.etLogin?.isEnabled = true
                    binding?.etPwd?.isEnabled = true
                    binding?.tvNext?.isEnabled = true
                }

                else -> {
                    binding?.vRoot?.transitionToStart()
                    binding?.etLogin?.isEnabled = true
                    binding?.etPwd?.isEnabled = true
                    binding?.tvNext?.isEnabled = true
                }
            }
        }
    }

    private fun debug() = false
}