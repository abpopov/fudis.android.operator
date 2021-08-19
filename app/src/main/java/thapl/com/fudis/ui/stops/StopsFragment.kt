package thapl.com.fudis.ui.stops

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import thapl.com.fudis.R
import thapl.com.fudis.databinding.FragmentStopsBinding
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.adapters.ProductsAdapter
import thapl.com.fudis.ui.base.BaseFragment

class StopsFragment : BaseFragment() {

    private val viewModel: StopsViewModel by sharedViewModel()

    private var _binding: FragmentStopsBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(GOOD_ID) { key, bundle ->
            bundle.getLong(key).let {
                viewModel.stopItem(it, true)
            }
        }
    }

    override fun onStop() {
        viewModel.search.postValue("")
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStopsBinding.inflate(inflater, container, false)
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
        if (binding?.rvStopList?.itemDecorationCount == 0) {
            binding?.rvStopList?.addItemDecoration(
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
        binding?.rvStopList?.adapter = ProductsAdapter(
            glide = null,
            viewModel = viewModel,
            click = { item, enable ->
                when (enable) {
                    true -> {
                        navigate(StopsFragmentDirections.actionConfirm(item.id))
                    }
                    false -> {
                        viewModel.stopItem(item.id, false)
                    }
                }
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
        viewModel.products.observe(viewLifecycleOwner, { result ->
            when (result) {
                is ResultEntity.Loading -> {
                    binding?.tvReload?.visibility = View.GONE
                    binding?.rvStopList?.visibility = View.VISIBLE
                }
                is ResultEntity.Error -> {
                    binding?.tvReload?.visibility = View.VISIBLE
                    binding?.rvStopList?.visibility = View.INVISIBLE
                }
                is ResultEntity.Success -> {
                    binding?.tvReload?.visibility = View.GONE
                    binding?.rvStopList?.visibility = View.VISIBLE
                    viewModel.search.postValue(viewModel.search.value)
                }
            }
        })
        viewModel.filteredProducts.observe(viewLifecycleOwner, { result ->
            if (result != null) {
                (binding?.rvStopList?.adapter as? ProductsAdapter)?.submitList(result)
            }
        })
    }

    companion object {
        const val GOOD_ID = "good_id"
    }
}