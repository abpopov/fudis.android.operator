package thapl.com.fudis.domain.case

import thapl.com.fudis.data.Repo
import thapl.com.fudis.domain.mapper.MenuApiToEntityMapper
import thapl.com.fudis.domain.mapper.OrderListApiToEntityMapper
import thapl.com.fudis.domain.mapper.ReceiptApiToEntityMapper
import thapl.com.fudis.domain.model.ErrorEntity
import thapl.com.fudis.domain.model.MenuEntity
import thapl.com.fudis.domain.model.ReceiptEntity
import thapl.com.fudis.utils.FudisException
import thapl.com.fudis.utils.addHeaders

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