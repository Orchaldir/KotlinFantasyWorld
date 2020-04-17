package util.log

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotSameAs
import io.mockk.mockk
import org.junit.jupiter.api.Test
import util.ecs.EcsState

class MessageLogTest {

    @Test
    fun `Test empty constructor`() {
        assertThat(MessageLog()).isEqualTo(MessageLog(emptyList()))
    }

    @Test
    fun `Add a message to the message log`() {
        val message0 = mockk<Message>()
        val message1 = mockk<Message>()
        val log = MessageLog(listOf(message0))

        val newLog = log.add(message1)

        assertThat(newLog).isEqualTo(MessageLog(listOf(message0, message1)))
    }

    @Test
    fun `Add a message to the state`() {
        val message0 = mockk<Message>()
        val message1 = mockk<Message>()
        val log = MessageLog(listOf(message0))
        val state = EcsState(dataMap = mapOf(MessageLog::class to log))

        val newState = addMessage(state, message1)

        assertThat(newState).isNotSameAs(state)

        val newLog = newState.getData<MessageLog>()

        assertThat(newLog).isEqualTo(MessageLog(listOf(message0, message1)))
    }

}