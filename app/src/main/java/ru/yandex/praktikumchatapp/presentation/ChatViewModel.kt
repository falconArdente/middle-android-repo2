package ru.yandex.praktikumchatapp.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository

class ChatViewModel(
    val isWithReplies: Boolean = true
) : ViewModel() {

    private val repository = ChatRepository()

    private val _chatState = MutableStateFlow(ChatState())

    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    //  Задание 4: замените messages и shouldShowKeyboard на state DONE

    init {
        viewModelScope.launch {
            while (isWithReplies) {
                repository.getReplyMessage().collect { response ->
                    val currentMessages = _chatState.value.messages
                    val newMessages = currentMessages + Message.OtherMessage(response)
                    _chatState.value = ChatState(
                        messages = newMessages,
                        shouldShowKeyboard = newMessages.isNotEmpty()
                    )
                }
            }
        }
    }

    fun sendMyMessage(messageText: String) {
        _chatState.update { currentState ->
            val currentMessages = currentState.messages
            currentState.copy(currentMessages + Message.MyMessage(messageText))
        }
    }
}