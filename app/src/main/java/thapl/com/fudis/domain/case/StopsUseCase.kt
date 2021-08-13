package thapl.com.fudis.domain.case

import thapl.com.fudis.domain.model.ProductEntity

interface StopsUseCase : BaseUseCase {
    suspend fun getProducts(): List<ProductEntity>
    suspend fun stopItem(id: Long, stop: Boolean): List<ProductEntity>
}