package com.example.stockchat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


import com.example.stockchat.databinding.ActivityStockDetailBinding

class StockDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStockDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val symbol = intent.getStringExtra("STOCK_SYMBOL")
        val name = intent.getStringExtra("STOCK_NAME")
        val price = intent.getDoubleExtra("STOCK_PRICE", 0.0)

        binding.detailSymbolTextView.text = symbol
        binding.detailNameTextView.text = name
        binding.detailPriceTextView.text = String.format("Price: %.2f", price)

        // TODO: Integrate MPAndroidChart here for historical data
        // Example:
        // val lineChart = binding.stockChart
        // setupChart(lineChart)
        // loadChartData(lineChart, symbol)
    }

    // You would add methods here to set up and populate the MPAndroidChart
    /*
    private fun setupChart(chart: LineChart) {
        // Basic chart setup
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)
        // ... more chart styling and axis configuration
    }

    private fun loadChartData(chart: LineChart, symbol: String) {
        // Fetch historical data using your StockRepository
        // Convert data to Entry objects and create LineDataSet
        // Set data to chart: chart.data = LineData(dataSet)
        // chart.invalidate()
    }
    */
}