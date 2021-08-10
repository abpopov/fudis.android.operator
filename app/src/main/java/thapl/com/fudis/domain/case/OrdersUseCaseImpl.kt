package thapl.com.fudis.domain.case

import thapl.com.fudis.data.Repo
import thapl.com.fudis.domain.mapper.OrderListApiToEntityMapper
import thapl.com.fudis.utils.addHeaders

class OrdersUseCaseImpl(private val repo: Repo) : OrdersUseCase {

    override fun getContext() = repo.getContext()

    override suspend fun getOrders() = with(repo.orders()) {
        OrderListApiToEntityMapper.map(this).addHeaders()
    }



}