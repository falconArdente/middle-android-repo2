package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen


class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    private val retryInitialDelay = 60L
    fun getReplyMessage(): Flow<String> {
        return api.getReply()
            .retryWhen { cause, attempt ->
                delay(retryInitialDelay * attempt)
                true
            }// Задание 2: добавьте обработку ошибок Done
    }
}