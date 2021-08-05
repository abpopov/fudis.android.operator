package thapl.com.fudis.ui.stops

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
import thapl.com.fudis.databinding.FragmentStopsBinding
import thapl.com.fudis.ui.base.BaseFragment

class StopsFragment : BaseFragment() {

    private val viewModel: StopsViewModel by sharedViewModel()

    private var _binding: FragmentStopsBinding? = null
    private val binding get() = _binding


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
    }

    private fun initListeners() {
        binding?.ivClear?.setOnClickListener {
            binding?.etSearch?.setText("")
        }
    }

    private fun initObservers() {

    }

    companion object {
        const val GOOD_ID = "good_id"
    }
}