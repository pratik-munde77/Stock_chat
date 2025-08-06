package com.example.stockchat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockchat.data.StockRepository
import com.example.stockchat.data.models.Stock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StockViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _trackedStocks = MutableStateFlow<List<Stock>>(emptyList())
    val trackedStocks: StateFlow<List<Stock>> = _trackedStocks.asStateFlow()

    private val _currentStockDetail = MutableStateFlow<Stock?>(null)
    val currentStockDetail: StateFlow<Stock?> = _currentStockDetail.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getTrackedStocks().collect { stocks ->
                _trackedStocks.value = stocks
            }
        }
    }

    fun searchStock(symbol: String) {
        viewModelScope.launch {
            val quote = repository.fetchStockQuote(symbol)
            quote?.let {
                _currentStockDetail.value = Stock(
                    symbol = it.symbol,
                    name = it.longName ?: it.symbol,
                    currentPrice = it.regularMarketPrice,
                    change = it.regularMarketChange,
                    changePercent = it.regularMarketChangePercent
                )
            } ?: run {
                _currentStockDetail.value = null // Stock not found or error
            }
        }
    }

    fun addStockToPortfolio(stock: Stock) {
        viewModelScope.launch {
            repository.addTrackedStock(stock)
        }
    }

    fun removeStockFromPortfolio(symbol: String) {
        viewModelScope.launch {
            repository.removeTrackedStock(symbol)
        }
    }

    fun refreshTrackedStocks() {
        viewModelScope.launch {
            // Re-fetch data for all tracked stocks
            _trackedStocks.value.forEach { stock ->
                val updatedQuote = repository.fetchStockQuote(stock.symbol)
                updatedQuote?.let {
                    repository.addTrackedStock(
                        stock.copy(
                            currentPrice = it.regularMarketPrice,
                            change = it.regularMarketChange,
                            changePercent = it.regularMarketChangePercent
                        )
                    )
                }
            }
        }
    }
}