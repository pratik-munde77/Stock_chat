package com.example.stockchat.data

import com.example.stockchat.data.models.QuoteResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// NOTE: Yahoo Finance API is complex and often requires authentication/specific endpoints.
// This is a highly simplified example. You might need to use a proxy or a different API.
interface StockApiService {
    @GET("v7/finance/quote") // Example endpoint, may not be accurate for Yahoo Finance
    suspend fun getStockQuote(@Query("symbols") symbols: String): Response<QuoteResponse>

    // Add other endpoints for historical data, search, etc.
}