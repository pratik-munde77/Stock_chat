package com.example.stockchat.data

import com.example.stockchat.data.models.Stock
import com.example.stockchat.data.models.Quote
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StockRepository @Inject constructor(
    private val stockApiService: StockApiService,
    private val stockDao: StockDao
) {

    // --- Remote Data (API) ---
    suspend fun fetchStockQuote(symbol: String): Quote? {
        return try {
            val response = stockApiService.getStockQuote(symbol)
            if (response.isSuccessful) {
                response.body()?.quoteResponse?.result?.firstOrNull()
            } else {
                // Handle API error
                null
            }
        } catch (e: Exception) {
            // Handle network or parsing error
            e.printStackTrace()
            null
        }
    }

    // --- Local Data (Room) ---
    fun getTrackedStocks(): Flow<List<Stock>> {
        return stockDao.getAllStocks()
    }

    suspend fun addTrackedStock(stock: Stock) {
        stockDao.insertStock(stock)
    }

    suspend fun removeTrackedStock(symbol: String) {
        stockDao.deleteStock(symbol)
    }
}