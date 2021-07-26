package thapl.com.fudis.domain.case

import thapl.com.fudis.data.Repo

class OrdersUseCaseImpl(private val repo: Repo) : OrdersUseCase {

    override fun getContext() = repo.getContext()

}