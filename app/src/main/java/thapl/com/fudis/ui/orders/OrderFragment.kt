package thapl.com.fudis.ui.orders

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import thapl.com.fudis.GlideApp
import thapl.com.fudis.R
import thapl.com.fudis.databinding.FragmentOrderBinding
import thapl.com.fudis.domain.model.OrderEntity
import thapl.com.fudis.domain.model.ResultEntity
import thapl.com.fudis.domain.model.SOURCE_TYPE_DC
import thapl.com.fudis.domain.model.SOURCE_TYPE_YA
import thapl.com.fudis.ui.adapters.CartAdapter
import thapl.com.fudis.ui.base.BaseFragment
import thapl.com.fudis.utils.toOrderAction
import thapl.com.fudis.utils.toOrderStatus
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderFragment : BaseFragment() {

    private val viewModel: OrdersViewModel by sharedViewModel()

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding

    private val args: OrderFragmentArgs by navArgs()

    private val formatter by lazy {
        NumberFormat.getNumberInstance().also {
            it.minimumFractionDigits = 0
            it.maximumFractionDigits = 2
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
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
        val item = args.order
        viewModel.initOrder(item)
        binding?.tvNumberOrder?.text = String.format("№ %s", item.id.toString())
        item.dcOrderId?.let {
            binding?.tvServiceName?.text = String.format("№ %s", it)
        } ?: run {
            binding?.tvServiceName?.text = ""
        }
        when (item.orderSource) {
            SOURCE_TYPE_YA -> binding?.ivLogoService?.setImageResource(R.drawable.ic_logo_yandex_eda)
            SOURCE_TYPE_DC -> binding?.ivLogoService?.setImageResource(R.drawable.ic_logo_delivery_club)
            else -> binding?.ivLogoService?.setImageResource(0)
        }
        item.createdAt?.let { date ->
            binding?.tvTimeValue?.text = SimpleDateFormat(
                "dd.MM.yy ${getString(R.string.order_time_at)} HH:mm",
                Locale.getDefault()
            ).format(Date(date))
        } ?: run {
            binding?.tvTimeValue?.text = ""
        }
        item.deliveryAt?.let { date ->
            val dateObj = Date(date)
            binding?.tvDueValue?.text = if (DateUtils.isToday(date)) {
                SimpleDateFormat(
                    "${getString(R.string.order_time_today)}, HH:mm",
                    Locale.getDefault()
                ).format(dateObj)
            } else {
                SimpleDateFormat("dd.MM.yy, HH:mm", Locale.getDefault()).format(dateObj)
            }
        } ?: run {
            binding?.tvDueValue?.text = ""
        }
        binding?.tvOrderTotalValue?.text = String.format("%s ₽", formatter.format(item.orderSum))
        item.conception?.let { c ->
            binding?.tvVendorName?.visibility = View.VISIBLE
            binding?.ivLogoVendor?.visibility = View.VISIBLE
            binding?.tvVendorName?.text = c.title
            binding?.ivLogoVendor?.let { iv ->
                c.logo?.let {
                    GlideApp.with(this)
                        .load(it)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .fallback(0)
                        .into(iv)
                } ?: run {
                    iv.setImageResource(0)
                }
            }
        } ?: run {
            binding?.tvVendorName?.visibility = View.GONE
            binding?.ivLogoVendor?.visibility = View.GONE
        }
        binding?.rvCartList?.adapter = CartAdapter(
            null,
            viewModel
        ) { receipt, _ ->
            navigate(OrderFragmentDirections.actionReceipt(receipt.item.id, item.id))
        }
        (binding?.rvCartList?.adapter as? CartAdapter)?.submitList(item.cartData)
        setChangeableData(item)
    }

    private fun initListeners() {
        binding?.tvBack?.setOnClickListener {
            navigate(OrderFragmentDirections.actionBack())
        }
        binding?.tvOrderAction?.setOnClickListener {

        }
    }

    private fun initObservers() {
        viewModel.order.observe(viewLifecycleOwner, { result ->
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

    private fun setChangeableData(item: OrderEntity) {
        val status = item.status.toOrderStatus()
        binding?.tvStatusValue?.text = getString(status)
        val action = item.status.toOrderAction()
        if (action != 0) {
            binding?.tvOrderAction?.visibility = View.VISIBLE
            binding?.tvOrderAction?.text = getString(action)
        } else {
            binding?.tvOrderAction?.visibility = View.INVISIBLE
        }
    }
}