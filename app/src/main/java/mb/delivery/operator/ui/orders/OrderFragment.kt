package mb.delivery.operator.ui.orders

import android.os.Bundle
import android.text.InputType
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import mb.delivery.operator.R
import mb.delivery.operator.data.api.model.EditProductApi
import mb.delivery.operator.data.api.model.OrderCartItemRequestApi
import mb.delivery.operator.data.api.model.OrderCartModificatorRequestApi
import mb.delivery.operator.databinding.FragmentOrderBinding
import mb.delivery.operator.domain.model.CartEntity
import mb.delivery.operator.domain.model.ORDER_STATUS_READY
import mb.delivery.operator.domain.model.OrderEntity
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.domain.model.SOURCE_TYPE_DC
import mb.delivery.operator.domain.model.SOURCE_TYPE_YA
import mb.delivery.operator.ui.adapters.CartAdapter
import mb.delivery.operator.ui.base.BaseFragment
import mb.delivery.operator.utils.toOrderAction
import mb.delivery.operator.utils.toOrderStatus
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderFragment : BaseFragment() {

    private val viewModel: OrdersViewModel by sharedViewModel()

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding

    private val args: OrderFragmentArgs by navArgs()

    private var pendingOrganizations: List<OrganizationKitchenEntity> = emptyList()
    private var pendingEditProducts: List<EditProductApi> = emptyList()
    private var draftCart: MutableList<OrderCartItemRequestApi> = mutableListOf()

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
        binding?.tvOrderTotalValue?.text = String.format("%s ₽", formatter.format(item.orderSum))
        binding?.rvCartList?.adapter = CartAdapter(
            null,
            viewModel
        ) { receipt, _ ->
            navigate(OrderFragmentDirections.actionReceipt(receipt.item.id, item.id))
        }
        setChangeableData(item)
        updateOperatorActionsVisibility()
    }

    private fun initListeners() {
        binding?.tvBack?.setOnClickListener {
            navigate(OrderFragmentDirections.actionBack())
        }
        binding?.tvOrderAction?.setOnClickListener {
            val order = viewModel.currentOrder.value
            order ?: return@setOnClickListener
            binding?.tvOrderAction?.isEnabled = false
            val next = order.getNextStatus()
            val allowWithoutReady = viewModel.allowStatusWithoutDishesReady.value == true
            if (next == ORDER_STATUS_READY && !allowWithoutReady && !order.itemsAreReady()) {
                Toast.makeText(it.context, R.string.order_status_error, Toast.LENGTH_LONG).show()
                binding?.tvOrderAction?.isEnabled = true
            } else {
                viewModel.changeStatus(order.id, next)
            }
        }
        binding?.tvEditComment?.setOnClickListener {
            showCommentDialog()
        }
        binding?.tvEditOrganization?.setOnClickListener {
            viewModel.loadOrganizations()
        }
        binding?.tvEditCart?.setOnClickListener {
            val order = viewModel.currentOrder.value ?: return@setOnClickListener
            draftCart = viewModel.toCartRequest(order.cartData).toMutableList()
            showCartEditDialog()
        }
        binding?.tvExportPos?.setOnClickListener {
            val order = viewModel.currentOrder.value ?: return@setOnClickListener
            if (!order.externalUuid.isNullOrBlank()) {
                Toast.makeText(requireContext(), R.string.order_export_pos_done, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding?.tvExportPos?.isEnabled = false
            viewModel.exportToPos(order.id)
        }
    }

    private fun initObservers() {
        viewModel.currentOrder.observe(viewLifecycleOwner) { result ->
            if (result?.id == args.order.id) {
                setChangeableData(result)
                updateOperatorActionsVisibility()
            }
        }
        viewModel.status.observe(viewLifecycleOwner) { result ->
            if (result is ResultEntity.Error) {
                Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
                binding?.tvOrderAction?.isEnabled = true
            }
        }
        viewModel.itemStatus.observe(viewLifecycleOwner) { result ->
            if (result is ResultEntity.Error) {
                Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.exportPos.observe(viewLifecycleOwner) { result ->
            binding?.tvExportPos?.isEnabled = true
            when (result) {
                is ResultEntity.Success -> {
                    Toast.makeText(requireContext(), R.string.order_export_pos_queued, Toast.LENGTH_SHORT).show()
                    updateOperatorActionsVisibility()
                }
                is ResultEntity.Error -> {
                    Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
        viewModel.allowEditOrder.observe(viewLifecycleOwner) {
            updateOperatorActionsVisibility()
        }
        viewModel.enableManualPosExport.observe(viewLifecycleOwner) {
            updateOperatorActionsVisibility()
        }
        viewModel.organizations.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultEntity.Success -> {
                    pendingOrganizations = result.data
                    showOrganizationDialog()
                }
                is ResultEntity.Error -> {
                    Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
        viewModel.editProducts.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultEntity.Success -> {
                    pendingEditProducts = result.data
                    showAddProductDialog()
                }
                is ResultEntity.Error -> {
                    Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    private fun updateOperatorActionsVisibility() {
        val allowEdit = viewModel.allowEditOrder.value == true
        val allowPos = viewModel.enableManualPosExport.value == true
        val order = viewModel.currentOrder.value
        binding?.llOperatorActions?.isVisible = allowEdit || allowPos
        binding?.tvEditComment?.isVisible = allowEdit
        binding?.tvEditOrganization?.isVisible = allowEdit
        binding?.tvEditCart?.isVisible = allowEdit
        binding?.tvExportPos?.isVisible = allowPos
        binding?.tvExportPos?.isEnabled = order?.externalUuid.isNullOrBlank()
        if (!order?.externalUuid.isNullOrBlank()) {
            binding?.tvExportPos?.text = getString(R.string.order_export_pos_done)
        } else {
            binding?.tvExportPos?.text = getString(R.string.order_export_pos)
        }
    }

    private fun showCommentDialog() {
        val order = viewModel.currentOrder.value ?: return
        val input = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            setText(order.clientComment.orEmpty())
            minLines = 3
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.order_edit_comment_title)
            .setView(input)
            .setPositiveButton(R.string.order_edit_save) { _, _ ->
                viewModel.updateOrder(order.id, clientComment = input.text?.toString().orEmpty())
            }
            .setNegativeButton(R.string.order_edit_cancel, null)
            .show()
    }

    private fun showOrganizationDialog() {
        val order = viewModel.currentOrder.value ?: return
        if (pendingOrganizations.isEmpty()) {
            Toast.makeText(requireContext(), R.string.order_edit_organization_title, Toast.LENGTH_SHORT).show()
            return
        }
        val titles = pendingOrganizations.map { it.title }.toTypedArray()
        val checked = pendingOrganizations.indexOfFirst { it.id == order.organizationId }.coerceAtLeast(0)
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.order_edit_organization_title)
            .setSingleChoiceItems(titles, checked) { dialog, which ->
                val selected = pendingOrganizations.getOrNull(which) ?: return@setSingleChoiceItems
                viewModel.updateOrder(order.id, organizationId = selected.id)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.order_edit_cancel, null)
            .show()
    }

    private fun showCartEditDialog() {
        val order = viewModel.currentOrder.value ?: return
        val labels = draftCart.map { item ->
            val product = order.cartData.find { it.item.id == item.catalogItemId }
            val title = product?.item?.baseTitle
                ?: product?.item?.title
                ?: pendingEditProducts.find { it.id == item.catalogItemId }?.title
                ?: "#${item.catalogItemId}"
            "${item.count} × $title"
        }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.order_edit_cart_title)
            .setItems(labels) { _, which ->
                showCartLineDialog(which)
            }
            .setNeutralButton(R.string.order_edit_cart_add) { _, _ ->
                viewModel.loadEditProducts(order.organizationId)
            }
            .setPositiveButton(R.string.order_edit_cart_save) { _, _ ->
                if (draftCart.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.order_edit_cart_title, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewModel.updateCart(order.id, draftCart.toList())
            }
            .setNegativeButton(R.string.order_edit_cancel, null)
            .show()
    }

    private fun showCartLineDialog(index: Int) {
        val item = draftCart.getOrNull(index) ?: return
        val options = arrayOf("+1", "-1", getString(R.string.order_edit_cart_remove))
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.order_edit_cart_title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> draftCart[index] = item.copy(count = item.count + 1)
                    1 -> {
                        if (item.count <= 1) {
                            draftCart.removeAt(index)
                        } else {
                            draftCart[index] = item.copy(count = item.count - 1)
                        }
                    }
                    2 -> draftCart.removeAt(index)
                }
                showCartEditDialog()
            }
            .setNegativeButton(R.string.order_edit_cancel) { _, _ ->
                showCartEditDialog()
            }
            .show()
    }

    private fun showAddProductDialog() {
        if (pendingEditProducts.isEmpty()) {
            Toast.makeText(requireContext(), R.string.order_edit_cart_add, Toast.LENGTH_SHORT).show()
            return
        }
        val titles = pendingEditProducts.map { it.title.orEmpty() }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.order_edit_cart_add)
            .setItems(titles) { _, which ->
                val product = pendingEditProducts.getOrNull(which) ?: return@setItems
                pickModifiersAndAdd(product)
            }
            .setNegativeButton(R.string.order_edit_cancel) { _, _ ->
                showCartEditDialog()
            }
            .show()
    }

    private fun pickModifiersAndAdd(product: EditProductApi) {
        val productId = product.id ?: return
        val mods = product.modificators.orEmpty().filter { it.id != null }
        if (mods.isEmpty()) {
            draftCart.add(
                OrderCartItemRequestApi(
                    catalogItemId = productId,
                    count = 1,
                    status = CartEntity.STATUS_NEW,
                    modificators = emptyList()
                )
            )
            showCartEditDialog()
            return
        }
        val titles = mods.map {
            val price = it.price ?: 0f
            "${it.title.orEmpty()} (+${formatter.format(price)} ₽)"
        }.toTypedArray()
        val checked = BooleanArray(mods.size)
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.order_edit_modifiers_title)
            .setMultiChoiceItems(titles, checked) { _, which, isChecked ->
                checked[which] = isChecked
            }
            .setPositiveButton(R.string.order_edit_save) { _, _ ->
                val selected = mods.mapIndexedNotNull { index, mod ->
                    if (!checked[index]) return@mapIndexedNotNull null
                    OrderCartModificatorRequestApi(
                        modificatorId = mod.id ?: return@mapIndexedNotNull null,
                        count = 1
                    )
                }
                draftCart.add(
                    OrderCartItemRequestApi(
                        catalogItemId = productId,
                        count = 1,
                        status = CartEntity.STATUS_NEW,
                        modificators = selected
                    )
                )
                showCartEditDialog()
            }
            .setNegativeButton(R.string.order_edit_cancel) { _, _ ->
                showCartEditDialog()
            }
            .show()
    }

    private fun setChangeableData(item: OrderEntity) {
        if (item.clientComment.isNullOrEmpty()) {
            binding?.tvClientComment?.visibility = View.GONE
        } else {
            binding?.tvClientComment?.visibility = View.VISIBLE
            binding?.tvClientComment?.text = getString(R.string.order_client_comment, item.clientComment)
        }
        if ((item.personsCount ?: 0) > 0) {
            binding?.tvForksValue?.visibility = View.VISIBLE
            binding?.tvForksLabel?.visibility = View.VISIBLE
            binding?.tvForksValue?.text = resources.getQuantityString(
                R.plurals.order_forks_value,
                item.personsCount ?: 0,
                item.personsCount ?: 0
            )
        } else {
            binding?.tvForksValue?.visibility = View.GONE
            binding?.tvForksLabel?.visibility = View.GONE
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
        val status = item.status.toOrderStatus()
        binding?.tvStatusValue?.text = getString(status)
        val action = item.status.toOrderAction()
        if (action != 0) {
            binding?.tvOrderAction?.isEnabled = true
            binding?.tvOrderAction?.visibility = View.VISIBLE
            binding?.tvOrderAction?.text = getString(action)
        } else {
            binding?.tvOrderAction?.visibility = View.INVISIBLE
        }
        (binding?.rvCartList?.adapter as? CartAdapter)?.submitList(mutableListOf<CartEntity>().apply {
            addAll(item.cartData)
            item.gift?.let { g ->
                add(
                    CartEntity(
                        item = g.copy(price = 0f),
                        count = 1,
                        modifiers = listOf(),
                        status = -1,
                        hasTechCard = false,
                        id = -1
                    )
                )
            }
        })
    }
}
