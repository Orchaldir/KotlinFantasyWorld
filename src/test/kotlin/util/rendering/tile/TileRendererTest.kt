package util.rendering.tile

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import javafx.scene.paint.Color
import org.junit.jupiter.api.BeforeEach
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
        renderer = mock()
        tileRenderer = TileRenderer(
            renderer,
            START_X,
            START_Y,
            TILE_WIDTH,
            TILE_HEIGHT
        )
    }

    @Test
    fun testRenderUnicode() {
        tileRenderer.renderUnicode("T", COLOR, 2, 7, 3)

        verifyUnicode("T")
    }

    private fun verifyUnicode(text: String) {
        val x = (START_X + TILE_WIDTH * 3.5).toInt()
        val y = (START_Y + TILE_HEIGHT * 8.5).toInt()

        verify(renderer).setColor(COLOR)
        verify(renderer).setFont(60)
        verify(renderer).renderUnicode(text, x, y)
        verifyNoMoreInteractions(renderer)
    }

    @Test
    fun testRenderFullTile() {
        tileRenderer.renderFullTile(COLOR, 3, 7)

        verify(renderer).setColor(COLOR)
        verify(renderer).renderRectangle(130, 340, 10, 20)
        verifyNoMoreInteractions(renderer)
    }

    // render tile

    @Test
    fun testRenderTileEmpty() {
        tileRenderer.renderTile(EmptyTile, 3, 7, 4)

        verifyNoMoreInteractions(renderer)
    }

    @Test
    fun testRenderTileFull() {
        tileRenderer.renderTile(FullTile(COLOR), 3, 7, 4)

        verify(renderer).setColor(COLOR)
        verify(renderer).renderRectangle(130, 340, 40, 80)
        verifyNoMoreInteractions(renderer)
    }

    @Test
    fun testRenderTileUnicode() {
        tileRenderer.renderTile(UnicodeTile("S", COLOR), 2, 7, 3)

        verifyUnicode("S")
    }
}