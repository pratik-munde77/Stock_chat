package com.example.stockchat.data.models

data class QuoteResponse(
    val quoteResponse: QuoteResult
)

data class QuoteResult(
    val result: List<Quote>
)

data class Quote(
    val symbol: String,
    val regularMarketPrice: Double,
    val regularMarketChange: Double,
    val regularMarketChangePercent: Double,
    val longName: String?,
    val currency: String?,
    val exchange: String?,
    val regularMarketOpen: Double?,
    val regularMarketDayHigh: Double?,
    val regularMarketDayLow: Double?,
    val regularMarketVolume: Long?,
    val regularMarketTime: Long? // Timestamp in seconds or milliseconds
)
