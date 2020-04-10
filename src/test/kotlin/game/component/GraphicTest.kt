package game.component

import assertk.assertThat
import assertk.assertions.isSameAs
import javafx.scene.paint.Color
import org.junit.jupiter.api.Test
import util.rendering.tile.EmptyTile
import util.rendering.tile.FullTile

class GraphicTest {

    private val tile = FullTile(Color.BLACK)

    @Test
    fun `List of 2 tiles`() {
        val graphic = Graphic(listOf(EmptyTile, tile))

        assertThat(graphic.get(-1)).isSameAs(DEFAULT_GRAPHIC)
        assertThat(graphic.get(0)).isSameAs(EmptyTile)
        assertThat(graphic.get(1)).isSameAs(tile)
        assertThat(graphic.get(2)).isSameAs(DEFAULT_GRAPHIC)
    }

    @Test
    fun `List of 1 tile`() {
        val graphic = Graphic(listOf(EmptyTile))

        assertThat(graphic.get(-1)).isSameAs(DEFAULT_GRAPHIC)
        assertThat(graphic.get(0)).isSameAs(EmptyTile)
        assertThat(graphic.get(1)).isSameAs(DEFAULT_GRAPHIC)
    }

    @Test
    fun `Single tile constructor`() {
        val graphic = Graphic(tile)

        assertThat(graphic.get(-1)).isSameAs(DEFAULT_GRAPHIC)
        assertThat(graphic.get(0)).isSameAs(tile)
        assertThat(graphic.get(1)).isSameAs(DEFAULT_GRAPHIC)
    }

}