package game.reducer

import assertk.assertThat
import assertk.assertions.isSameAs
import game.component.BigBody
import game.component.SimpleBody
import game.component.SnakeBody
import game.map.GameMapBuilder
import game.map.Terrain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.math.Direction.NORTH
import util.math.Direction.WEST
import kotlin.test.assertNull

private val MAP = GameMapBuilder(3, 3, Terrain.FLOOR)
    .setTerrain(0, 0, Terrain.WALL)
    .build()

class MoveReducerTest {

    @Nested
    inner class GetNewPositionOfSimpleBody {

        @Test
        fun `Move a simple body`() {
            val body = SimpleBody(4)

            assertThat(getNewPosition(MAP, 0, body, NORTH)).isSameAs(1)
        }

        @Test
        fun `Fail to move a simple body outside the map`() {
            val body = SimpleBody(1)

            assertNull(getNewPosition(MAP, 0, body, NORTH))
        }

        @Test
        fun `Fail to move a simple body into a wall`() {
            val body = SimpleBody(1)

            assertNull(getNewPosition(MAP, 0, body, WEST))
        }
    }

    @Nested
    inner class GetNewPositionOfBigBody {

        @Test
        fun `Move a big body`() {
            val body = BigBody(4, 2)

            assertThat(getNewPosition(MAP, 0, body, NORTH)).isSameAs(1)
        }

        @Test
        fun `Fail to move a big body outside the map`() {
            val body = BigBody(1, 2)

            assertNull(getNewPosition(MAP, 0, body, NORTH))
        }

        @Test
        fun `Fail to move a big body into a wall`() {
            val body = BigBody(1, 2)

            assertNull(getNewPosition(MAP, 0, body, WEST))
        }
    }

    @Nested
    inner class GetNewPositionOfSnakeBody {

        @Test
        fun `Move a big body`() {
            val body = SnakeBody(listOf(4, 3, 6))

            assertThat(getNewPosition(MAP, 0, body, NORTH)).isSameAs(1)
        }

        @Test
        fun `Fail to move a big body outside the map`() {
            val body = SnakeBody(listOf(1, 4, 3))

            assertNull(getNewPosition(MAP, 0, body, NORTH))
        }

        @Test
        fun `Fail to move a big body into a wall`() {
            val body = SnakeBody(listOf(1, 4, 3))

            assertNull(getNewPosition(MAP, 0, body, WEST))
        }
    }
}