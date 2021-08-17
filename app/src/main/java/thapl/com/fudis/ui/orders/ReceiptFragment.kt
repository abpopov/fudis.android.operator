package thapl.com.fudis.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import thapl.com.fudis.R
import thapl.com.fudis.databinding.FragmentReceiptBinding
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.base.BaseFragment

class ReceiptFragment : BaseFragment() {

    private val viewModel: OrdersViewModel by sharedViewModel()

    private var _binding: FragmentReceiptBinding? = null
    private val binding get() = _binding

    private val args: ReceiptFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReceiptBinding.inflate(inflater, container, false)
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
        viewModel.initReceipt(args.id)
        if (args.order == -1L) {
            binding?.tvBack?.text = getString(R.string.receipt_back_menu)
        } else {
            binding?.tvBack?.text = getString(R.string.receipt_back, args.order.toString())
        }
    }

    private fun initListeners() {
        binding?.tvBack?.setOnClickListener {
            navigate(ReceiptFragmentDirections.actionBack())
        }
    }

    private fun initObservers() {
        viewModel.receipt.observe(viewLifecycleOwner, { result ->
            when (result) {
                is ResultEntity.Loading -> {

                }
                is ResultEntity.Error -> {

                }
                is ResultEntity.Success -> {

                }
            }
        })
    }
}