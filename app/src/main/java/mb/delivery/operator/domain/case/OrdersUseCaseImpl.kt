package mb.delivery.operator.domain.case

import mb.delivery.operator.data.Repo
import mb.delivery.operator.data.api.model.EditProductApi
import mb.delivery.operator.data.api.model.OrderApi
import mb.delivery.operator.data.api.model.OrderCartItemRequestApi
import mb.delivery.operator.domain.mapper.MenuApiToEntityMapper
import mb.delivery.operator.domain.mapper.OrderApiToEntityMapper
import mb.delivery.operator.domain.mapper.OrderListApiToEntityMapper
import mb.delivery.operator.domain.mapper.OrganizationKitchenIndexApiToEntityMapper
import mb.delivery.operator.domain.mapper.ReceiptApiToEntityMapper
import mb.delivery.operator.domain.model.ErrorEntity
import mb.delivery.operator.domain.model.MenuEntity
import mb.delivery.operator.domain.model.OrderEntity
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.ReceiptEntity
import mb.delivery.operator.utils.FudisException
import mb.delivery.operator.utils.addHeaders

class OrdersUseCaseImpl(private val repo: Repo) : OrdersUseCase {

    override fun getContext() = repo.getContext()

    override fun getShowMenu() = repo.getShowMenu()

    override fun getShowOrders() = repo.getShowOrders()

    override fun getShowStopList() = repo.getShowStopList()

    override fun getShowHighload() = repo.getShowHighload()

    override fun getAllowStatusWithoutDishesReady() = repo.getAllowStatusWithoutDishesReady()

    override fun getAllowEditOrder() = repo.getAllowEditOrder()

    override fun getEnableManualPosExport() = repo.getEnableManualPosExport()

    override suspend fun getOrders() = with(repo.orders()) {
        OrderListApiToEntityMapper.map(items).addHeaders()
    }

    override suspend fun getReceipt(id: Long): ReceiptEntity {
        return ReceiptApiToEntityMapper.map(repo.receipt(id)) ?: throw FudisException(
            ErrorEntity(
                code = 777,
                textCode = "server",
                type = "FudisException",
                message = ""
            )
        )
    }

    override suspend fun changeStatus(id: Long, status: Int): OrderEntity {
        return mapOrderOrThrow(repo.changeStatus(id, status))
    }

    override suspend fun changeItemStatus(item: Int, status: Int): OrderEntity {
        return mapOrderOrThrow(repo.changeItemStatus(item, status))
    }

    override suspend fun updateOrder(id: Long, clientComment: String?, organizationId: Int?): OrderEntity {
        return mapOrderOrThrow(repo.updateOrder(id, clientComment, organizationId))
    }

    override suspend fun updateCart(id: Long, cartItems: List<OrderCartItemRequestApi>): OrderEntity {
        return mapOrderOrThrow(repo.updateCart(id, cartItems))
    }

    override suspend fun exportToPos(id: Long): OrderEntity {
        val response = repo.exportToPos(id)
        return mapOrderOrThrow(response.order)
    }

    override suspend fun editProducts(organizationId: Int): List<EditProductApi> {
        return repo.editProducts(organizationId).items.orEmpty()
    }

    override suspend fun organizations(): List<OrganizationKitchenEntity> {
        return OrganizationKitchenIndexApiToEntityMapper.map(repo.organizations())?.items.orEmpty()
    }

    private fun mapOrderOrThrow(api: OrderApi?): OrderEntity {
        return OrderApiToEntityMapper.map(api) ?: throw FudisException(
            ErrorEntity(
                code = 777,
                textCode = "server",
                type = "FudisException",
                message = responseMessage(api)
            )
        )
    }

    private fun responseMessage(api: OrderApi?): String {
        return if (api == null) "Пустой ответ сервера" else ""
    }

    override suspend fun getMenu(): MenuEntity {
        val result = repo.menu()
        repo.setShowMenu(result.showMenu)
        repo.setShowOrders(result.showOrders)
        repo.setShowStopList(result.showStopList)
        repo.setShowHighload(result.showHighload)
        repo.setAllowStatusWithoutDishesReady(result.allowStatusWithoutDishesReady)
        repo.setAllowEditOrder(result.allowEditOrder)
        repo.setEnableManualPosExport(result.enableManualPosExport)
        return MenuApiToEntityMapper.map(result)
    }

}
