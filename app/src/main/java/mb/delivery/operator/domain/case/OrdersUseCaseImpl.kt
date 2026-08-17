package mb.delivery.operator.domain.case

import mb.delivery.operator.data.Repo
import mb.delivery.operator.domain.mapper.MenuApiToEntityMapper
import mb.delivery.operator.domain.mapper.OrderListApiToEntityMapper
import mb.delivery.operator.domain.mapper.ReceiptApiToEntityMapper
import mb.delivery.operator.domain.model.ErrorEntity
import mb.delivery.operator.domain.model.MenuEntity
import mb.delivery.operator.domain.model.ReceiptEntity
import mb.delivery.operator.utils.FudisException
import mb.delivery.operator.utils.addHeaders

class OrdersUseCaseImpl(private val repo: Repo) : OrdersUseCase {

    override fun getContext() = repo.getContext()

    override fun getShowMenu() = repo.getShowMenu()

    override fun getShowOrders() = repo.getShowOrders()

    override fun getShowStopList() = repo.getShowMenu()

    override fun getShowHighload() = repo.getShowHighload()

    override suspend fun getOrders() = with(repo.orders()) {
        OrderListApiToEntityMapper.map(this).addHeaders()
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

    override suspend fun changeStatus(id: Long, status: Int): Pair<Long, Int> {
        return Pair(id, checkNotNull(repo.changeStatus(id, status).status))
    }

    override suspend fun changeItemStatus(item: Int, status: Int): Pair<Int, Int> {
        return Pair(item, checkNotNull(repo.changeItemStatus(item, status).status))
    }

    override suspend fun getMenu(): MenuEntity {
        val result = repo.menu()
        repo.setShowMenu(result.showMenu)
        repo.setShowOrders(result.showOrders)
        repo.setShowStopList(result.showStopList)
        repo.setShowHighload(result.showHighload)
        return MenuApiToEntityMapper.map(result)
    }

}