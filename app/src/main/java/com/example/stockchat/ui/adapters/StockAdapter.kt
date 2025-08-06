package com.example.stockchat.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stockchat.data.models.Stock
import com.example.stockchat.databinding.ItemStockBinding

class StockAdapter(private val onItemClicked: (Stock) -> Unit) :
    ListAdapter<Stock, StockAdapter.StockViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = ItemStockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val currentStock = getItem(position)
        holder.bind(currentStock)
        holder.itemView.setOnClickListener {
            onItemClicked(currentStock)
        }
    }

    class StockViewHolder(private val binding: ItemStockBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stock: Stock) {
            binding.stockSymbolTextView.text = stock.symbol
            binding.stockNameTextView.text = stock.name
            binding.stockPriceTextView.text = String.format("%.2f", stock.currentPrice)

            val changeText = String.format("%.2f (%.2f%%)", stock.change, stock.changePercent)
            binding.stockChangeTextView.text = changeText

            // Set color based on change
            if (stock.change > 0) {
                binding.stockChangeTextView.setTextColor(Color.GREEN)
            } else if (stock.change < 0) {
                binding.stockChangeTextView.setTextColor(Color.RED)
            } else {
                binding.stockChangeTextView.setTextColor(Color.GRAY)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Stock>() {
            override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
                return oldItem.symbol == newItem.symbol
            }

            override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
                return oldItem == newItem
            }
        }
    }
}