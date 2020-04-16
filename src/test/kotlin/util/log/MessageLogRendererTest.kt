package util.log

import io.mockk.*
import javafx.scene.paint.Color.*
import org.junit.jupiter.api.Test
import util.math.Size
import util.rendering.tile.TileRenderer

const val STRING0 = "1.Message"
const val STRING1 = "Another Message!"
const val STRING2 = "Too many messages!"
val message0 = Message(STRING0, WHITE)
val message1 = Message(STRING1, RED)
val message2 = Message(STRING2, BLUE)
val MESSAGE_LOG = MessageLog(listOf(message0, message1))
val SIZE = Size(100, 100)

class MessageLogRendererTest {

    @Test
    fun `Render empty message log`() {
        val tileRenderer = mockk<TileRenderer>()
        val renderer = MessageLogRenderer(x = 10, y = 20, size = SIZE, fontSize = 1)

        renderer.render(tileRenderer, MessageLog(emptyList()))

        confirmVerified(tileRenderer)
    }

    @Test
    fun `Render message log with size 1`() {
        val tileRenderer = mockk<TileRenderer>()
        val renderer = MessageLogRenderer(x = 10, y = 20, size = SIZE, fontSize = 1)

        every { tileRenderer.renderText(any(), any(), any(), any(), any()) } just runs

        renderer.render(tileRenderer, MESSAGE_LOG)

        verify { tileRenderer.renderText(STRING1, RED, 10, 20, 1) }
        verify { tileRenderer.renderText(STRING0, WHITE, 10, 21, 1) }
        confirmVerified(tileRenderer)
    }

    @Test
    fun `Render message log with size 2`() {
        val tileRenderer = mockk<TileRenderer>()
        val renderer = MessageLogRenderer(x = 15, y = 25, size = SIZE, fontSize = 2)

        every { tileRenderer.renderText(any(), any(), any(), any(), any()) } just runs

        renderer.render(tileRenderer, MESSAGE_LOG)

        verify { tileRenderer.renderText(STRING1, RED, 15, 25, 2) }
        verify { tileRenderer.renderText(STRING0, WHITE, 15, 27, 2) }
        confirmVerified(tileRenderer)
    }

    @Test
    fun `Too many message`() {
        val tileRenderer = mockk<TileRenderer>()
        val renderer = MessageLogRenderer(x = 15, y = 25, size = Size(100, 2))

        every { tileRenderer.renderText(any(), any(), any(), any(), any()) } just runs

        renderer.render(tileRenderer, MessageLog(listOf(message0, message1, message2)))

        verify { tileRenderer.renderText(STRING2, BLUE, 15, 25, 1) }
        verify { tileRenderer.renderText(STRING1, RED, 15, 26, 1) }
        confirmVerified(tileRenderer)
    }
}