package util.log

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isNotSameAs
import io.mockk.mockk
import org.junit.jupiter.api.Test

class MessageLogTest {

    @Test
    fun `Add a message to the message log`() {
        val message0 = mockk<Message>()
        val message1 = mockk<Message>()
        val log = MessageLog(listOf(message0))

        val newLog = log.add(message1)

        assertThat(newLog).isNotSameAs(log)
        assertThat(newLog.messages).containsExactly(message0, message1)
    }

}