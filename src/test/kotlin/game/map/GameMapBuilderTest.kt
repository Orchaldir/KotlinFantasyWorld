package game.map

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import game.map.Terrain.FLOOR
import game.map.Terrain.WALL
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.math.Size
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

private const val ENTITY0 = 1
private const val ENTITY1 = 2

class GameMapBuilderTest {

    @Test
    fun `Test Constructor`() {
        val builder = GameMapBuilder(2, 3, FLOOR)

        assertThat(builder.size).isEqualTo(Size(2, 3))

        for (i in 0 until 6) {
            assertThat(builder.getTerrain(i)).isEqualTo(FLOOR)
        }
    }

    @Test
    fun `Add border`() {
        val builder = GameMapBuilder(3, 4, FLOOR)
            .addBorder(WALL)

        assertBorder(builder.getTerrainList())
    }

    @Nested
    inner class AddRectangle {

        @Test
        fun `Add a rectangle with type WALL`() {
            val builder = GameMapBuilder(4, 5, FLOOR)
                .addRectangle(0, 0, 3, 4, WALL)

            assertThat(builder.getTerrainList()).containsExactly(
                WALL, WALL, WALL, FLOOR,
                WALL, FLOOR, WALL, FLOOR,
                WALL, FLOOR, WALL, FLOOR,
                WALL, WALL, WALL, FLOOR,
                FLOOR, FLOOR, FLOOR, FLOOR
            )
        }

        @Test
        fun `With invalid start`() {
            val builder = GameMapBuilder(4, 5, FLOOR)

            assertFailsWith<IllegalArgumentException> { builder.addRectangle(-1, 0, 3, 4, WALL) }
            assertFailsWith<IllegalArgumentException> { builder.addRectangle(0, -1, 3, 4, WALL) }
        }

        @Test
        fun `With invalid size`() {
            val builder = GameMapBuilder(4, 5, FLOOR)

            assertFailsWith<IllegalArgumentException> { builder.addRectangle(0, 0, -1, 4, WALL) }
            assertFailsWith<IllegalArgumentException> { builder.addRectangle(0, 0, 3, -1, WALL) }
        }

        @Test
        fun `Rectangle reaches Outside`() {
            val builder = GameMapBuilder(4, 5, FLOOR)

            assertFailsWith<IllegalArgumentException> { builder.addRectangle(1, 1, 4, 4, WALL) }
            assertFailsWith<IllegalArgumentException> { builder.addRectangle(1, 1, 3, 5, WALL) }
        }
    }

    @Test
    fun `Set terrain from FLOOR to WALL`() {
        val builder = GameMapBuilder(2, 2, FLOOR)
            .setTerrain(1, 0, WALL)

        assertThat(builder.getTerrainList()).containsExactly(
            FLOOR, WALL,
            FLOOR, FLOOR
        )

        assertEquals(WALL, builder.getTerrain(1))
        assertEquals(WALL, builder.getTerrain(1, 0))
    }

    @Nested
    inner class SetEntity {

        @Test
        fun `Set entity on map`() {
            val builder = GameMapBuilder(2, 2, FLOOR)
                .setEntity(1, ENTITY0)

            assertThat(builder.getEntity(1)).isSameAs(ENTITY0)
        }

        @Test
        fun `Set entity of size 2`() {
            val builder = GameMapBuilder(2, 3, FLOOR)
                .setEntity(index = 2, entity = ENTITY0, size = 2)

            assertThat(builder.getEntity(2)).isSameAs(ENTITY0)
            assertThat(builder.getEntity(3)).isSameAs(ENTITY0)
            assertThat(builder.getEntity(4)).isSameAs(ENTITY0)
            assertThat(builder.getEntity(5)).isSameAs(ENTITY0)
        }

        @Test
        fun `Overwrite entity with itself`() {
            val builder = GameMapBuilder(2, 2, FLOOR)
                .setEntity(1, ENTITY0)
                .setEntity(1, ENTITY0)

            assertThat(builder.getEntity(1)).isSameAs(ENTITY0)
        }

        @Test
        fun `Overwrite entity of size 2 with itself`() {
            val builder = GameMapBuilder(2, 2, FLOOR)
                .setEntity(2, ENTITY0)
                .setEntity(index = 0, entity = ENTITY0, size = 2)

            assertThat(builder.getEntity(0)).isSameAs(ENTITY0)
            assertThat(builder.getEntity(1)).isSameAs(ENTITY0)
            assertThat(builder.getEntity(2)).isSameAs(ENTITY0)
            assertThat(builder.getEntity(3)).isSameAs(ENTITY0)
        }

        @Test
        fun `Overwrite other entity`() {
            assertFailsWith<IllegalArgumentException>("Overwritten entity $ENTITY0 with $ENTITY1 at index 1!") {
                GameMapBuilder(2, 2, FLOOR)
                    .setEntity(1, ENTITY0)
                    .setEntity(1, ENTITY1)
            }
        }

        @Test
        fun `Overwrite other entity with size 2`() {
            assertFailsWith<IllegalArgumentException>("Overwritten entity $ENTITY0 with $ENTITY1 at index 1!") {
                GameMapBuilder(2, 2, FLOOR)
                    .setEntity(1, ENTITY0)
                    .setEntity(index = 0, entity = ENTITY1, size = 2)
            }
        }

    }

    @Nested
    inner class RemoveEntity {

        @Test
        fun `Remove entity from map`() {
            val builder = GameMapBuilder(2, 2, FLOOR)
                .setEntity(1, ENTITY0)
                .removeEntity(1, ENTITY0)

            assertNull(builder.getEntity(1))
        }

        @Test
        fun `Remove entity with size 2`() {
            val builder = GameMapBuilder(2, 2, FLOOR)
                .setEntity(index = 0, entity = ENTITY0, size = 2)
                .removeEntity(index = 0, entity = ENTITY0, size = 2)

            assertNull(builder.getEntity(0))
            assertNull(builder.getEntity(1))
            assertNull(builder.getEntity(2))
            assertNull(builder.getEntity(3))
        }

        @Test
        fun `Remove entity that is not there`() {
            assertFailsWith<IllegalArgumentException>("Removed null instead of $ENTITY0 at index 1!") {
                GameMapBuilder(2, 2, FLOOR)
                    .removeEntity(1, ENTITY0)
            }
        }

        @Test
        fun `Remove wrong entity`() {
            assertFailsWith<IllegalArgumentException>("Removed $ENTITY0 instead of $ENTITY1 at index 3!") {
                GameMapBuilder(2, 2, FLOOR)
                    .setEntity(3, ENTITY0)
                    .removeEntity(3, ENTITY1)
            }
        }

        @Test
        fun `Remove wrong entity with size 2`() {
            assertFailsWith<IllegalArgumentException>("Removed $ENTITY0 instead of $ENTITY1 at index 0!") {
                GameMapBuilder(2, 2, FLOOR)
                    .setEntity(index = 0, entity = ENTITY0, size = 2)
                    .removeEntity(index = 0, entity = ENTITY1, size = 2)
            }
        }

    }

    @Test
    fun `Build game map`() {
        val map = GameMapBuilder(3, 4, FLOOR)
            .addBorder(WALL).build()

        assertBorder(map.terrainList)
    }

    private fun assertBorder(list: List<Terrain>) {
        assertThat(list).containsExactly(
            WALL, WALL, WALL,
            WALL, FLOOR, WALL,
            WALL, FLOOR, WALL,
            WALL, WALL, WALL
        )
    }

}
