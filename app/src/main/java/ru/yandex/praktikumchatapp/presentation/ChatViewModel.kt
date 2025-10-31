package ru.yandex.praktikumchatapp.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository

class ChatViewModel(
    val isWithReplies: Boolean = true
) : ViewModel() {

    private val repository = ChatRepository()

    private val _chatState = MutableStateFlow<ChatState>(ChatState())

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
        viewModelScope.launch(Dispatchers.IO) {
            val currentMessages = _chatState.value.messages
            _chatState.value =
                _chatState.value.copy(currentMessages + Message.MyMessage(messageText))
        }
    }
}