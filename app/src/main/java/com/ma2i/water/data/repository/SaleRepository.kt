package com.ma2i.water.data.repository

import com.ma2i.water.data.model.Sale

/**
 * Repository interface used by the example ViewModel.
 * Implement this in your codebase to call DAO/Room.
 */
interface SaleRepository {
    suspend fun saveSale(sale: Sale)
}
