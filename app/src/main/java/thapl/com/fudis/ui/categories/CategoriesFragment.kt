package thapl.com.fudis.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import thapl.com.fudis.R
import thapl.com.fudis.databinding.FragmentCategoriesBinding
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.adapters.CategoriesAdapter
import thapl.com.fudis.ui.base.BaseFragment

class CategoriesFragment : BaseFragment() {

    private val viewModel: CategoriesViewModel by sharedViewModel()

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
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
        binding?.etSearch?.doAfterTextChanged {
            binding?.ivClear?.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
        if (binding?.rvCategoriesList?.itemDecorationCount == 0) {
            binding?.rvCategoriesList?.addItemDecoration(
                DividerItemDecoration(
                    requireContext(), LinearLayoutManager.VERTICAL
                ).also {
                    ContextCompat.getDrawable(requireContext(), R.drawable.divider)?.let { d ->
                        it.setDrawable(d)
                    }
                }
            )
        }
        binding?.etSearch?.doAfterTextChanged {
            viewModel.search.postValue(it?.toString())
        }
        binding?.rvCategoriesList?.adapter = CategoriesAdapter(
            glide = null,
            viewModel = viewModel,
            click = { item, enable ->

            }
        )
    }

    private fun initListeners() {
        binding?.ivClear?.setOnClickListener {
            binding?.etSearch?.setText("")
        }
        binding?.tvReload?.setOnClickListener {
            viewModel.refresh()
        }
    }

    private fun initObservers() {
        viewModel.categories.observe(viewLifecycleOwner, { result ->
            when (result) {
                is ResultEntity.Loading -> {
                    binding?.tvReload?.visibility = View.GONE
                    binding?.rvCategoriesList?.visibility = View.VISIBLE
                }
                is ResultEntity.Error -> {
                    binding?.tvReload?.visibility = View.VISIBLE
                    binding?.rvCategoriesList?.visibility = View.INVISIBLE
                }
                is ResultEntity.Success -> {
                    binding?.tvReload?.visibility = View.GONE
                    binding?.rvCategoriesList?.visibility = View.VISIBLE
                    viewModel.search.postValue(viewModel.search.value)
                }
            }
        })
        viewModel.filteredCategories.observe(viewLifecycleOwner, { result ->
            if (result != null) {
                (binding?.rvCategoriesList?.adapter as? CategoriesAdapter)?.submitList(result)
            }
        })
    }
}