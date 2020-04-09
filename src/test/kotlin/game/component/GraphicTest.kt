package game.component

import assertk.assertThat
import assertk.assertions.isSameAs
import javafx.scene.paint.Color
import org.junit.Test
import util.rendering.tile.EmptyTile
import util.rendering.tile.FullTile

class GraphicTest {

    @Test
    fun `Get tile with index`() {
        val tile0 = EmptyTile
        val tile1 = FullTile(Color.BLACK)
        val graphic = Graphic(listOf(tile0, tile1))

        assertThat(graphic.get(0)).isSameAs(tile0)
        assertThat(graphic.get(1)).isSameAs(tile1)
    }

    @Test
    fun `Get default tile with invalid index`() {
        val graphic = Graphic(listOf(EmptyTile))

        assertThat(graphic.get(-1)).isSameAs(DEFAULT_GRAPHIC)
        assertThat(graphic.get(1)).isSameAs(DEFAULT_GRAPHIC)
    }

}