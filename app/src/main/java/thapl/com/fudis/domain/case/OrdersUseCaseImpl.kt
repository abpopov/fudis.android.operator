package thapl.com.fudis.domain.case

import thapl.com.fudis.data.Repo
import thapl.com.fudis.domain.mapper.OrderListApiToEntityMapper
import thapl.com.fudis.domain.mapper.ReceiptApiToEntityMapper
import thapl.com.fudis.domain.model.ErrorEntity
import thapl.com.fudis.domain.model.ReceiptEntity
import thapl.com.fudis.utils.FudisException
import thapl.com.fudis.utils.addHeaders

class OrdersUseCaseImpl(private val repo: Repo) : OrdersUseCase {

    override fun getContext() = repo.getContext()

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

}