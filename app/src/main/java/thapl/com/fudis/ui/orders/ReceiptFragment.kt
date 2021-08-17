package thapl.com.fudis.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import thapl.com.fudis.GlideApp
import thapl.com.fudis.R
import thapl.com.fudis.databinding.FragmentReceiptBinding
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.ui.adapters.ReceiptAdapter
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
                    binding?.vTitlePlaceholder?.visibility = View.VISIBLE
                    binding?.vTitlePlaceholder?.startAnim()
                    binding?.vDescPlaceholder?.visibility = View.VISIBLE
                    binding?.vDescPlaceholder?.startAnim()
                    binding?.vImgPlaceholder?.visibility = View.VISIBLE
                    binding?.vImgPlaceholder?.startAnim()
                    binding?.tvTitleText?.text = ""
                    binding?.tvDescText?.text = ""
                    binding?.tvWeightText?.text = ""
                    binding?.ivFoodPicture?.setImageResource(0)
                    binding?.rvReceiptList?.adapter = null
                }
                is ResultEntity.Error -> {
                    binding?.vTitlePlaceholder?.visibility = View.GONE
                    binding?.vTitlePlaceholder?.stopAnim()
                    binding?.vDescPlaceholder?.visibility = View.GONE
                    binding?.vDescPlaceholder?.stopAnim()
                    binding?.vImgPlaceholder?.visibility = View.GONE
                    binding?.vImgPlaceholder?.stopAnim()
                    Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
                }
                is ResultEntity.Success -> {
                    //if (result.data.id == args.id) {
                    binding?.vTitlePlaceholder?.visibility = View.GONE
                    binding?.vTitlePlaceholder?.stopAnim()
                    binding?.vDescPlaceholder?.visibility = View.GONE
                    binding?.vDescPlaceholder?.stopAnim()
                    binding?.vImgPlaceholder?.visibility = View.GONE
                    binding?.vImgPlaceholder?.stopAnim()
                    binding?.tvTitleText?.text = result.data.title
                    binding?.tvDescText?.text = result.data.description
                    binding?.tvWeightText?.text =
                        getString(R.string.receipt_weight, result.data.doneWeight)
                    binding?.ivFoodPicture?.let { v ->
                        result.data.image?.let { i ->
                            val radius = v.context.resources.getDimensionPixelSize(R.dimen.dp12)
                            GlideApp.with(this)
                                .load(i)
                                .transform(CenterCrop(), RoundedCorners(radius))
                                .placeholder(0)
                                .fallback(0)
                                .error(0)
                                .into(v)
                        } ?: run {
                            v.setImageResource(0)
                        }

                    }
                    binding?.rvReceiptList?.adapter = ReceiptAdapter(
                        null,
                        viewModel
                    ) { _, _ ->

                    }
                    (binding?.rvReceiptList?.adapter as? ReceiptAdapter)?.submitList(
                        result.data.products.mapIndexed { index, receiptProductEntity ->
                            receiptProductEntity.also {
                                it.counter = (index + 1).toString()
                            }
                        }
                    )
                    //}
                }
            }
        })
    }
}