package util.rendering.tile

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import javafx.scene.paint.Color
import util.rendering.Renderer
import kotlin.test.BeforeTest
import kotlin.test.Test

private const val START_X = 100
private const val START_Y = 200
private const val TILE_WIDTH = 10
private const val TILE_HEIGHT = 20
private val COLOR = Color.RED

class TileRendererTest {

    private lateinit var renderer: Renderer
    private lateinit var tileRenderer: TileRenderer

    @BeforeTest
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

        val x = (START_X + TILE_WIDTH * 3.5).toInt()
        val y = (START_Y + TILE_HEIGHT * 8.5).toInt()

        verify(renderer).setColor(COLOR)
        verify(renderer).setFont(60)
        verify(renderer).renderUnicode("T", x, y)
        verifyNoMoreInteractions(renderer)
    }

    @Test
    fun testRenderTile() {
        tileRenderer.renderTile(COLOR, 3, 7)

        verify(renderer).setColor(COLOR)
        verify(renderer).renderRectangle(130, 340, 10, 20)
        verifyNoMoreInteractions(renderer)
    }
}