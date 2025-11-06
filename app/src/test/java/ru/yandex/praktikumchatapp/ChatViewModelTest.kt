import app.cash.turbine.test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(isWithReplies = false)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `send message should update state with MyMessage`() = runTest {
        val message = Message.MyMessage("TestMessage")
        viewModel.sendMyMessage(message.text)
        viewModel.chatState.test {
            assert(awaitItem().messages.contains(message))
        }
        // Задание 5: допишите юнит-тест Done
    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }
        val scope = CoroutineScope(Job())
        val jobList: MutableList<Job> = mutableListOf()
        messagesToSend.forEach { message ->
            jobList.add(
                scope.launch(Dispatchers.IO) {
                    viewModel.sendMyMessage(message.text)
                }
            )
        }
        jobList.joinAll()
        viewModel.chatState.test {
            val actualList = awaitItem().messages
            messagesToSend.forEach {message ->
                assert(actualList.contains(message))
            }
            assert(actualList.size == messagesToSend.size)
        }
        // Задание 6: допишите юнит-тест Done
    }
}