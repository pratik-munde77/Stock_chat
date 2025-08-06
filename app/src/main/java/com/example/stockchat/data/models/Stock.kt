package com.example.stockchat.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks")
data class Stock(
    @PrimaryKey val symbol: String,
    val name: String,
    var currentPrice: Double,
    var change: Double,
    var changePercent: Double
)
