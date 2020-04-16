package util.log

import io.mockk.*
import javafx.scene.paint.Color.RED
import javafx.scene.paint.Color.WHITE
import org.junit.jupiter.api.Test
import util.rendering.tile.TileRenderer

const val STRING0 = "1.Message"
const val STRING1 = "Another Message!"
val MESSAGE_LOG = MessageLog(listOf(Message(STRING0, WHITE), Message(STRING1, RED)))

class MessageLogRendererTest {

    @Test
    fun `Render empty message log`() {
        val tileRenderer = mockk<TileRenderer>()
        val renderer = MessageLogRenderer(x = 10, y = 20, size = 1)

        renderer.render(tileRenderer, MessageLog(emptyList()))

        confirmVerified(tileRenderer)
    }

    @Test
    fun `Render message log with size 1`() {
        val tileRenderer = mockk<TileRenderer>()
        val renderer = MessageLogRenderer(x = 10, y = 20, size = 1)

        every { tileRenderer.renderText(any(), any(), any(), any(), any()) } just runs

        renderer.render(tileRenderer, MESSAGE_LOG)

        verify { tileRenderer.renderText(STRING1, RED, 10, 20, 1) }
        verify { tileRenderer.renderText(STRING0, WHITE, 10, 21, 1) }
        confirmVerified(tileRenderer)
    }

    @Test
    fun `Render message log with size 2`() {
        val tileRenderer = mockk<TileRenderer>()
        val renderer = MessageLogRenderer(x = 15, y = 25, size = 2)

        every { tileRenderer.renderText(any(), any(), any(), any(), any()) } just runs

        renderer.render(tileRenderer, MESSAGE_LOG)

        verify { tileRenderer.renderText(STRING1, RED, 15, 25, 2) }
        verify { tileRenderer.renderText(STRING0, WHITE, 15, 27, 2) }
        confirmVerified(tileRenderer)
    }
}