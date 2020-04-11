package game.reducer

import assertk.assertThat
import assertk.assertions.isSameAs
import game.component.BigBody
import game.component.SimpleBody
import game.component.SnakeBody
import game.map.GameMapBuilder
import game.map.Terrain
import game.map.Terrain.FLOOR
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.math.Direction.NORTH
import util.math.Direction.WEST
import util.math.Size
import kotlin.test.assertNull

private const val ENTITY = 1
private val MAP = GameMapBuilder(3, 3, FLOOR)
    .setTerrain(0, 0, Terrain.WALL)
    .build()

class MoveReducerTest {

    @Nested
    inner class GetNewPositionOfSimpleBody {

        @Test
        fun `Move a simple body`() {
            val body = SimpleBody(4)

            assertThat(getNewPosition(MAP, ENTITY, body, NORTH)).isSameAs(1)
        }

        @Test
        fun `Fail to move a simple body outside the map`() {
            val body = SimpleBody(1)

            assertNull(getNewPosition(MAP, ENTITY, body, NORTH))
        }

        @Test
        fun `Fail to move a simple body into a wall`() {
            val body = SimpleBody(1)

            assertNull(getNewPosition(MAP, ENTITY, body, WEST))
        }
    }

    @Nested
    inner class GetNewPositionOfBigBody {

        @Test
        fun `Move a big body`() {
            val body = BigBody(4, 2)

            assertThat(getNewPosition(MAP, ENTITY, body, NORTH)).isSameAs(1)
        }

        @Test
        fun `Fail to move a big body outside the map`() {
            val body = BigBody(1, 2)

            assertNull(getNewPosition(MAP, ENTITY, body, NORTH))
        }

        @Test
        fun `Fail to move a big body into a wall`() {
            val body = BigBody(1, 2)

            assertNull(getNewPosition(MAP, ENTITY, body, WEST))
        }
    }

    @Nested
    inner class GetNewPositionOfSnakeBody {

        @Test
        fun `Move a big body`() {
            val body = SnakeBody(listOf(4, 3, 6))

            assertThat(getNewPosition(MAP, ENTITY, body, NORTH)).isSameAs(1)
        }

        @Test
        fun `Fail to move a big body outside the map`() {
            val body = SnakeBody(listOf(1, 4, 3))

            assertNull(getNewPosition(MAP, ENTITY, body, NORTH))
        }

        @Test
        fun `Fail to move a big body into a wall`() {
            val body = SnakeBody(listOf(1, 4, 3))

            assertNull(getNewPosition(MAP, ENTITY, body, WEST))
        }
    }

    @Nested
    inner class UpdateEntityOnMap {

        @Test
        fun `Update a simple body on the map`() {
            val body = SimpleBody(4)
            val map = GameMapBuilder(Size(3, 3), FLOOR)
                .setEntity(4, ENTITY)
                .build()

            val newMap = updateEntityOnMap(map, ENTITY, body, 1)

            assertNull(newMap.entities[4])
            assertThat(newMap.entities[1]).isSameAs(ENTITY)
        }

        @Test
        fun `Update a big body on the map`() {
            val body = BigBody(4, 2)
            val map = GameMapBuilder(Size(3, 3), FLOOR)
                .setEntity(index = 4, entity = ENTITY, size = 2)
                .build()

            val newMap = updateEntityOnMap(map, ENTITY, body, 1)

            assertThat(newMap.entities[1]).isSameAs(ENTITY)
            assertThat(newMap.entities[2]).isSameAs(ENTITY)
            assertThat(newMap.entities[4]).isSameAs(ENTITY)
            assertThat(newMap.entities[5]).isSameAs(ENTITY)

            assertNull(newMap.entities[7])
            assertNull(newMap.entities[8])
        }

        @Test
        fun `Update a snake body on the map`() {
            val body = SnakeBody(listOf(4, 3, 6))
            val map = GameMapBuilder(Size(3, 3), FLOOR)
                .setEntity(4, ENTITY)
                .setEntity(3, ENTITY)
                .setEntity(6, ENTITY)
                .build()

            val newMap = updateEntityOnMap(map, ENTITY, body, 1)

            assertThat(newMap.entities[1]).isSameAs(ENTITY)
            assertThat(newMap.entities[4]).isSameAs(ENTITY)
            assertThat(newMap.entities[3]).isSameAs(ENTITY)

            assertNull(newMap.entities[6])
        }

        @Test
        fun `Update a snake body with 2 body parts in its last cell`() {
            val body = SnakeBody(listOf(4, 3, 6, 6))
            val map = GameMapBuilder(Size(3, 3), FLOOR)
                .setEntity(4, ENTITY)
                .setEntity(3, ENTITY)
                .setEntity(6, ENTITY)
                .build()

            val newMap = updateEntityOnMap(map, ENTITY, body, 1)

            assertThat(newMap.entities[1]).isSameAs(ENTITY)
            assertThat(newMap.entities[4]).isSameAs(ENTITY)
            assertThat(newMap.entities[3]).isSameAs(ENTITY)
            assertThat(newMap.entities[6]).isSameAs(ENTITY)
        }
    }
}