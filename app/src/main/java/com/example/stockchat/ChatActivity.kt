package com.example.stockchat


import androidx.activity.enableEdgeToEdge

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stockchat.databinding.ActivityChatBinding
import com.example.stockchat.ui.adapters.MessageAdapter
import com.example.stockchat.viewmodels.ChatViewModel
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        messageAdapter = MessageAdapter()
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true // New messages appear at the bottom
            }
            adapter = messageAdapter
        }

        // Observe messages from ViewModel
        lifecycleScope.launch {
            chatViewModel.messages.collect { messages ->
                messageAdapter.submitList(messages)
                binding.chatRecyclerView.scrollToPosition(messages.size - 1) // Scroll to latest message
            }
        }

        binding.sendButton.setOnClickListener {
            val messageText = binding.messageEditText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                chatViewModel.sendMessage(messageText)
                binding.messageEditText.text.clear()
            }
        }
    }
}
