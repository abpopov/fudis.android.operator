package mb.delivery.operator.ui.orders

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import mb.delivery.operator.R
import mb.delivery.operator.data.api.model.EditProductApi
import mb.delivery.operator.data.api.model.OrderCartItemRequestApi
import mb.delivery.operator.data.api.model.OrderCartModificatorRequestApi
import mb.delivery.operator.databinding.FragmentOrderBinding
import mb.delivery.operator.domain.model.CartEntity
import mb.delivery.operator.domain.model.CommentMode
import mb.delivery.operator.domain.model.ORDER_STATUS_READY
import mb.delivery.operator.domain.model.OrderEntity
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.domain.model.SOURCE_TYPE_DC
import mb.delivery.operator.domain.model.SOURCE_TYPE_YA
import mb.delivery.operator.ui.adapters.CartAdapter
import mb.delivery.operator.ui.base.BaseFragment
import mb.delivery.operator.ui.dialogs.CancelOrderConfirmDialog
import mb.delivery.operator.ui.dialogs.CartEditDialog
import mb.delivery.operator.ui.dialogs.CommentEditDialog
import mb.delivery.operator.ui.dialogs.ModifiersPickDialog
import mb.delivery.operator.ui.dialogs.OrganizationPickDialog
import mb.delivery.operator.ui.dialogs.ProductPickDialog
import mb.delivery.operator.utils.hasOrderAction
import mb.delivery.operator.utils.toOrderActionLabel
import mb.delivery.operator.utils.toOrderStatusLabel
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

    private val formatter by lazy {
        NumberFormat.getNumberInstance().also {
            it.minimumFractionDigits = 0
            it.maximumFractionDigits = 2
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(CommentEditDialog.REQUEST_KEY) { _, bundle ->
            val order = viewModel.currentOrder.value ?: return@setFragmentResultListener
            val comment = bundle.getString(CommentEditDialog.RESULT_COMMENT).orEmpty()
            viewModel.updateOrder(order.id, clientComment = comment)
        }
        setFragmentResultListener(OrganizationPickDialog.REQUEST_KEY) { _, bundle ->
            val order = viewModel.currentOrder.value ?: return@setFragmentResultListener
            val orgId = bundle.getInt(OrganizationPickDialog.RESULT_ORG_ID, 0)
            if (orgId > 0) {
                viewModel.updateOrder(order.id, organizationId = orgId)
            }
        }
        setFragmentResultListener(CartEditDialog.REQUEST_KEY) { _, bundle ->
            when (bundle.getString(CartEditDialog.RESULT_ACTION)) {
                CartEditDialog.ACTION_SAVE -> {
                    val order = viewModel.currentOrder.value ?: return@setFragmentResultListener
                    viewModel.updateCart(order.id, viewModel.cartEditDraft.toList())
                    viewModel.clearCartEdit()
                }
                CartEditDialog.ACTION_ADD -> {
                    val order = viewModel.currentOrder.value ?: return@setFragmentResultListener
                    viewModel.loadEditProducts(order.organizationId)
                }
            }
        }
        setFragmentResultListener(ProductPickDialog.REQUEST_KEY) { _, bundle ->
            if (bundle.getBoolean(ProductPickDialog.RESULT_CANCELLED, false)) {
                CartEditDialog.show(parentFragmentManager)
                return@setFragmentResultListener
            }
            val productId = bundle.getLong(ProductPickDialog.RESULT_PRODUCT_ID, 0L)
            val product = pendingEditProducts.find { it.id == productId }
            if (product == null) {
                CartEditDialog.show(parentFragmentManager)
                return@setFragmentResultListener
            }
            pickModifiersAndAdd(product)
        }
        setFragmentResultListener(ModifiersPickDialog.REQUEST_KEY) { _, bundle ->
            if (bundle.getBoolean(ModifiersPickDialog.RESULT_CANCELLED, false)) {
                CartEditDialog.show(parentFragmentManager)
                return@setFragmentResultListener
            }
            val productId = bundle.getLong(ModifiersPickDialog.RESULT_PRODUCT_ID, 0L)
            val modIds = bundle.getLongArray(ModifiersPickDialog.RESULT_MOD_IDS)?.toList().orEmpty()
            addProductToDraft(
                productId,
                modIds.map { OrderCartModificatorRequestApi(modificatorId = it, count = 1) }
            )
        }
        setFragmentResultListener(CancelOrderConfirmDialog.REQUEST_KEY) { _, bundle ->
            if (!bundle.getBoolean(CancelOrderConfirmDialog.RESULT_CONFIRMED, false)) {
                return@setFragmentResultListener
            }
            val order = viewModel.currentOrder.value ?: return@setFragmentResultListener
            viewModel.cancelOrder(order.id)
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
        binding?.ivOrderMenu?.setOnClickListener { anchor ->
            val order = viewModel.currentOrder.value ?: return@setOnClickListener
            if (!order.canCancel()) return@setOnClickListener
            PopupMenu(anchor.context, anchor).apply {
                menu.add(0, 1, 0, R.string.order_cancel)
                setOnMenuItemClickListener {
                    CancelOrderConfirmDialog.show(parentFragmentManager)
                    true
                }
                show()
            }
        }
        binding?.tvOrderAction?.setOnClickListener {
            val order = viewModel.currentOrder.value
            order ?: return@setOnClickListener
            binding?.tvOrderAction?.isEnabled = false
            val next = order.getNextStatus()
            if (next == ORDER_STATUS_READY && !viewModel.skipDishReadyChecks() && !order.itemsAreReady()) {
                Toast.makeText(it.context, R.string.order_status_error, Toast.LENGTH_LONG).show()
                binding?.tvOrderAction?.isEnabled = true
            } else {
                viewModel.changeStatus(order.id, next)
            }
        }
        binding?.tvEditComment?.setOnClickListener {
            val order = viewModel.currentOrder.value ?: return@setOnClickListener
            CommentEditDialog.show(parentFragmentManager, order.clientComment)
        }
        binding?.tvEditOrganization?.setOnClickListener {
            viewModel.loadOrganizations()
        }
        binding?.tvEditCart?.setOnClickListener {
            val order = viewModel.currentOrder.value ?: return@setOnClickListener
            val items = viewModel.toCartRequest(order.cartData)
            val titles = items.map { item ->
                val product = order.cartData.find { it.item.id == item.catalogItemId }
                product?.item?.baseTitle
                    ?: product?.item?.title
                    ?: "#${item.catalogItemId}"
            }
            viewModel.startCartEdit(items, titles)
            CartEditDialog.show(parentFragmentManager)
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
        viewModel.orderClosed.observe(viewLifecycleOwner) { id ->
            if (id == args.order.id) {
                navigate(OrderFragmentDirections.actionBack())
            }
        }
        viewModel.itemStatus.observe(viewLifecycleOwner) { result ->
            if (result is ResultEntity.Error) {
                Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.pendingItemChanges.observe(viewLifecycleOwner) {
            (binding?.rvCartList?.adapter as? CartAdapter)?.notifyPendingStateChanged()
        }
        viewModel.pendingItemTick.observe(viewLifecycleOwner) {
            (binding?.rvCartList?.adapter as? CartAdapter)?.refreshPendingCountdown()
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
        viewModel.cartItemReadyEnabled.observe(viewLifecycleOwner) {
            (binding?.rvCartList?.adapter as? CartAdapter)?.notifyPendingStateChanged()
        }
        viewModel.allowEditOrder.observe(viewLifecycleOwner) {
            updateOperatorActionsVisibility()
        }
        viewModel.enableManualPosExport.observe(viewLifecycleOwner) {
            updateOperatorActionsVisibility()
        }
        viewModel.showOrganization.observe(viewLifecycleOwner) {
            updateOperatorActionsVisibility()
        }
        viewModel.commentMode.observe(viewLifecycleOwner) {
            updateOperatorActionsVisibility()
            viewModel.currentOrder.value?.let { order -> setChangeableData(order) }
        }
        viewModel.organizations.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultEntity.Success -> {
                    pendingOrganizations = result.data
                    val order = viewModel.currentOrder.value ?: return@observe
                    if (pendingOrganizations.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            R.string.order_edit_organization_title,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@observe
                    }
                    OrganizationPickDialog.show(
                        parentFragmentManager,
                        pendingOrganizations,
                        order.organizationId
                    )
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
                    viewModel.editProductsCatalog = result.data
                    pendingEditProducts = result.data.items
                    if (pendingEditProducts.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            R.string.order_edit_cart_add,
                            Toast.LENGTH_SHORT
                        ).show()
                        CartEditDialog.show(parentFragmentManager)
                        return@observe
                    }
                    ProductPickDialog.show(parentFragmentManager)
                }
                is ResultEntity.Error -> {
                    Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
                    CartEditDialog.show(parentFragmentManager)
                }
                else -> Unit
            }
        }
    }

    private fun updateOperatorActionsVisibility() {
        val allowEdit = viewModel.allowEditOrder.value == true
        val allowPos = viewModel.enableManualPosExport.value == true
        val showOrg = viewModel.showOrganization.value == true
        val canEditComment = CommentMode.canEdit(viewModel.commentMode.value)
        val order = viewModel.currentOrder.value
        val showCart = allowEdit
        val showOrganizationBtn = allowEdit && showOrg
        val showCommentBtn = allowEdit && canEditComment
        binding?.llOperatorActions?.isVisible = showCart || showOrganizationBtn || showCommentBtn || allowPos
        binding?.tvEditComment?.isVisible = showCommentBtn
        binding?.tvEditOrganization?.isVisible = showOrganizationBtn
        binding?.tvEditCart?.isVisible = showCart
        binding?.tvExportPos?.isVisible = allowPos
        binding?.tvExportPos?.isEnabled = order?.externalUuid.isNullOrBlank()
        if (!order?.externalUuid.isNullOrBlank()) {
            binding?.tvExportPos?.text = getString(R.string.order_export_pos_done)
        } else {
            binding?.tvExportPos?.text = getString(R.string.order_export_pos)
        }
    }

    private fun pickModifiersAndAdd(product: EditProductApi) {
        val productId = product.id ?: return
        val mods = product.modificators.orEmpty().filter { it.id != null }
        if (mods.isEmpty()) {
            addProductToDraft(productId, emptyList())
            return
        }
        ModifiersPickDialog.show(parentFragmentManager, productId, mods, formatter)
    }

    private fun addProductToDraft(
        productId: Long,
        modificators: List<OrderCartModificatorRequestApi>
    ) {
        val title = pendingEditProducts.find { it.id == productId }.let { p ->
            p?.title?.takeIf { it.isNotBlank() } ?: p?.baseTitle ?: "#$productId"
        }
        viewModel.cartEditDraft.add(
            OrderCartItemRequestApi(
                catalogItemId = productId,
                count = 1,
                status = CartEntity.STATUS_NEW,
                modificators = modificators
            )
        )
        viewModel.cartEditTitles.add(title)
        CartEditDialog.show(parentFragmentManager)
    }

    private fun setChangeableData(item: OrderEntity) {
        val commentVisible = CommentMode.isVisible(viewModel.commentMode.value)
            && !item.clientComment.isNullOrEmpty()
        if (!commentVisible) {
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
        binding?.tvStatusValue?.text = getString(item.toOrderStatusLabel())
        binding?.ivOrderMenu?.isVisible = item.canCancel()
        if (item.hasOrderAction()) {
            binding?.tvOrderAction?.isEnabled = true
            binding?.tvOrderAction?.visibility = View.VISIBLE
            binding?.tvOrderAction?.text = getString(item.toOrderActionLabel())
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
