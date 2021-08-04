package thapl.com.fudis.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import thapl.com.fudis.R
import thapl.com.fudis.data.api.model.BAD_LOGIN
import thapl.com.fudis.databinding.FragmentAuthBinding
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.base.BaseFragment
import thapl.com.fudis.utils.*

class RegisterFragment : BaseFragment() {

    private val viewModel: RegisterViewModel by sharedViewModel()

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
            viewModel.setValidate(it)
        }
    }

    private fun initListeners() {
        binding?.vRoot?.setOnClickListener {
            binding?.vRoot?.transitionToStart()
        }
        binding?.tvNext?.setOnClickListener {
            if (validator?.completeValidate() == true) {
                context?.hideKeyboard(binding?.etLogin, binding?.etPwd)
                /*viewModel.auth(
                    binding?.etLogin?.text?.toString()?.trim(),
                    binding?.etPwd?.text?.toString()?.trim()
                )*/
                viewModel.auth(
                    "ContentM",
                    "dsfdsskenerJDD825MdskjdsdNdfk-34#@jsdlKkdasMljsd"
                )
            }
        }
    }

    private fun initObservers() {
        viewModel.authValidate.observe(viewLifecycleOwner, { enable ->
            binding?.tvNext?.isEnabled = enable
        })
        viewModel.authResult.observe(viewLifecycleOwner, { result ->
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
                null -> {
                    binding?.vRoot?.transitionToStart()
                    binding?.etLogin?.isEnabled = true
                    binding?.etPwd?.isEnabled = true
                    binding?.tvNext?.isEnabled = true
                }
            }
        })
    }
}