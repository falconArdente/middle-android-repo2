package ru.yandex.praktikumchatapp

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import ru.yandex.praktikumchatapp.data.ChatApi
import ru.yandex.praktikumchatapp.data.ChatRepository

@OptIn(ExperimentalCoroutinesApi::class)
class ChatRepositoryTest {
    private var testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var chatRepository: ChatRepository
    private val chatApi: ChatApi = mock()

    @Before
    fun setup() {
        chatRepository = ChatRepository(chatApi)
    }

    @After
    fun teardown() {
    }

    @Test
    fun `getReplyMessage should return a non-empty string`() = runTest(testDispatcher) {
        val replyText = "Hello"
        `when`(chatApi.getReply())
            .thenReturn(
                flow {
                    emit(replyText)
                }
            )
        chatRepository.getReplyMessage().test {
            assert(awaitItem().isNotEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getReplyMessage should retry on error then successfully return string`() = runTest {
        val replyText = "Hello"
        var isException = true

        `when`(chatApi.getReply())
            .thenReturn(
                flow {
                    if (isException) {
                        isException = false
                        throw Exception("test exception")
                    }
                    emit(replyText)
                }
            )

        chatRepository.getReplyMessage().test {
            assert(awaitItem() == replyText)
            cancelAndIgnoreRemainingEvents()
        }
    }
}