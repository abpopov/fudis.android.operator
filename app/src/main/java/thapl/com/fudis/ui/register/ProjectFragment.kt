package thapl.com.fudis.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.viewModel
import thapl.com.fudis.databinding.FragmentProjectBinding
import thapl.com.fudis.ui.base.BaseFragment
import thapl.com.fudis.utils.EmailValidator
import thapl.com.fudis.utils.Validate
import thapl.com.fudis.utils.ViewsValidator
import thapl.com.fudis.utils.hideKeyboard

class ProjectFragment : BaseFragment() {

    private val model: RegisterViewModel by viewModel()

    private var _binding: FragmentProjectBinding? = null
    private val binding get() = _binding

    private var validator: Validate? = null

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
        binding?.tvNext?.setOnClickListener {
            if (validator?.completeValidate() == true) {
                context?.hideKeyboard(binding?.etProject)
                model.setProject(binding?.etProject?.text?.toString()?.trim())
                navigate(ProjectFragmentDirections.actionAuth())
            }
        }
    }

    private fun initObservers() {
        model.authValidate.observe(viewLifecycleOwner) { enable ->
            binding?.tvNext?.isEnabled = enable
        }
    }
}