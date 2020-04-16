package util.rendering.tile

import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import javafx.scene.paint.Color
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.rendering.Renderer

private const val START_X = 100
private const val START_Y = 200
private const val TILE_WIDTH = 10
private const val TILE_HEIGHT = 20
private val COLOR = Color.RED

class TileRendererTest {

    private lateinit var renderer: Renderer
    private lateinit var tileRenderer: TileRenderer

    @BeforeEach
    fun setup() {
        renderer = mockk(relaxed = true)
        tileRenderer = TileRenderer(
            renderer,
            START_X,
            START_Y,
            TILE_WIDTH,
            TILE_HEIGHT
        )
    }

    @Nested
    inner class RenderText {

        @Test
        fun `Render text`() {
            tileRenderer.renderText("Test!", COLOR, 2, 7, 1)

            verify { renderer.setColor(COLOR) }
            verify { renderer.setFont(TILE_HEIGHT) }
            verifyText("T", 2, 7)
            verifyText("e", 3, 7)
            verifyText("s", 4, 7)
            verifyText("t", 5, 7)
            verifyText("!", 6, 7)
            confirmVerified(renderer)
        }

        @Test
        fun `Render text with size 2`() {
            tileRenderer.renderText("AB", COLOR, 3, 4, 2)

            verify { renderer.setColor(COLOR) }
            verify { renderer.setFont(TILE_HEIGHT * 2) }
            verifyText(text = "A", x = 3, y = 4, size = 2)
            verifyText(text = "B", x = 5, y = 4, size = 2)
            confirmVerified(renderer)
        }

        private fun verifyText(text: String, x: Int, y: Int, size: Int = 1) {
            val centerX = (START_X + TILE_WIDTH * (x + size / 2.0)).toInt()
            val centerY = (START_Y + TILE_HEIGHT * (y + size / 2.0)).toInt()

            verify { renderer.renderUnicode(text, centerX, centerY) }
        }
    }

    @Test
    fun testRenderUnicodeTile() {
        tileRenderer.renderUnicodeTile("T", COLOR, 2, 7, 3)

        verifyUnicode("T")
    }

    private fun verifyUnicode(text: String) {
        val x = (START_X + TILE_WIDTH * 3.5).toInt()
        val y = (START_Y + TILE_HEIGHT * 8.5).toInt()

        verify { renderer.setColor(COLOR) }
        verify { renderer.setFont(60) }
        verify { renderer.renderUnicode(text, x, y) }
        confirmVerified(renderer)
    }

    @Test
    fun testRenderFullTile() {
        tileRenderer.renderFullTile(COLOR, 3, 7)

        verify { renderer.setColor(COLOR) }
        verify { renderer.renderRectangle(130, 340, 10, 20) }
        confirmVerified(renderer)
    }

    // render tile

    @Test
    fun testRenderTileEmpty() {
        tileRenderer.renderTile(EmptyTile, 3, 7, 4)

        confirmVerified(renderer)
    }

    @Test
    fun testRenderTileFull() {
        tileRenderer.renderTile(FullTile(COLOR), 3, 7, 4)

        verify { renderer.setColor(COLOR) }
        verify { renderer.renderRectangle(130, 340, 40, 80) }
        confirmVerified(renderer)
    }

    @Test
    fun testRenderTileUnicode() {
        tileRenderer.renderTile(UnicodeTile("S", COLOR), 2, 7, 3)

        verifyUnicode("S")
    }
}