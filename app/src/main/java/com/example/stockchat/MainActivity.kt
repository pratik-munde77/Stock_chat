package com.example.stockchat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.example.stockchat.data.AppDatabase
import com.example.stockchat.data.StockApiService
import com.example.stockchat.data.StockRepository
import com.example.stockchat.data.models.Stock
import com.example.stockchat.databinding.ActivityMainBinding
import com.example.stockchat.ui.adapters.StockAdapter
import com.example.stockchat.utils.Constants
import com.example.stockchat.viewmodels.StockViewModel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var stockViewModel: StockViewModel
    private lateinit var stockAdapter: StockAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // --- Setup Retrofit ---
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_YAHOO_FINANCE) // Replace with actual Yahoo Finance API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
        val stockApiService = retrofit.create(StockApiService::class.java)

        // --- Setup Room Database ---
        val db = AppDatabase.getDatabase(applicationContext) // Assuming you have a getDatabase method in AppDatabase
        val stockDao = db.stockDao()

        // --- Setup Repository and ViewModel ---
        val stockRepository = StockRepository(stockApiService, stockDao)
        stockViewModel = ViewModelProvider(this, StockViewModelFactory(stockRepository))
            .get(StockViewModel::class.java)

        // --- Setup RecyclerView ---
        stockAdapter = StockAdapter { stock ->
            // Handle stock item click (e.g., navigate to StockDetailActivity)
            val intent = Intent(this, StockDetailActivity::class.java).apply {
                putExtra("STOCK_SYMBOL", stock.symbol)
                putExtra("STOCK_NAME", stock.name)
                putExtra("STOCK_PRICE", stock.currentPrice)
            }
            startActivity(intent)
        }
        binding.trackedStocksRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = stockAdapter
        }

        // Observe tracked stocks from ViewModel
        lifecycleScope.launch {
            stockViewModel.trackedStocks.collect { stocks ->
                stockAdapter.submitList(stocks)
            }
        }

        binding.searchButton.setOnClickListener {
            val symbol = binding.stockSymbolEditText.text.toString().trim().uppercase()
            if (symbol.isNotEmpty()) {
                stockViewModel.searchStock(symbol)
            } else {
                Toast.makeText(this, "Please enter a stock symbol", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe current stock detail for search results
        lifecycleScope.launch {
            stockViewModel.currentStockDetail.collect { stock ->
                stock?.let {
                    binding.stockNameTextView.text = it.name
                    binding.stockPriceTextView.text = String.format("%.2f", it.currentPrice)
                    binding.stockChangeTextView.text = String.format("%.2f (%.2f%%)", it.change, it.changePercent)
                    binding.addStockButton.isEnabled = true
                } ?: run {
                    binding.stockNameTextView.text = "Stock Not Found"
                    binding.stockPriceTextView.text = ""
                    binding.stockChangeTextView.text = ""
                    binding.addStockButton.isEnabled = false
                }
            }
        }

        binding.addStockButton.setOnClickListener {
            stockViewModel.currentStockDetail.value?.let { stock ->
                stockViewModel.addStockToPortfolio(stock)
                Toast.makeText(this, "${stock.name} added to portfolio", Toast.LENGTH_SHORT).show()
            }
        }

        binding.chatButton.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Initial refresh of tracked stocks
        stockViewModel.refreshTrackedStocks()
    }
}