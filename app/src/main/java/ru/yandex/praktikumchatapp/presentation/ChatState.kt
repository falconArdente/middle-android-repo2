package ru.yandex.praktikumchatapp.presentation

/**
 * Описывает состояние для экрана чата
 */
data class ChatState(
    val messages: List<Message> = emptyList(),
    val shouldShowKeyboard: Boolean = false
)
