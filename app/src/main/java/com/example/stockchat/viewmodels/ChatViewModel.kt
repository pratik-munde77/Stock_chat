package com.example.stockchat.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.stockchat.data.models.Message
import com.example.stockchat.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val chatRef = database.getReference(Constants.FIREBASE_CHAT_PATH)

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _currentUserDisplayName = MutableStateFlow<String?>("")
    val currentUserDisplayName: StateFlow<String?> = _currentUserDisplayName.asStateFlow()

    init {
        // Listen for chat messages
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = mutableListOf<Message>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)
                    message?.let { messageList.add(it) }
                }
                _messages.value = messageList.sortedBy { it.timestamp }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                println("Failed to load messages: ${error.message}")
            }
        })

        // Get current user's display name
        auth.currentUser?.let {
            _currentUserDisplayName.value = it.displayName ?: it.email?.split('@')?.get(0) ?: "Anonymous"
        }
    }

    fun sendMessage(text: String) {
        val currentUser = auth.currentUser
        if (currentUser != null && text.isNotBlank()) {
            val message = Message(
                senderId = currentUser.uid,
                senderName = _currentUserDisplayName.value ?: "Anonymous",
                text = text,
                timestamp = System.currentTimeMillis()
            )
            chatRef.push().setValue(message)
        }
    }
}