package game.map

import assertk.assertThat
import assertk.assertions.containsExactly
import game.map.Terrain.FLOOR
import game.map.Terrain.WALL
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GameMapBuilderTest {

    @Test
    fun `Test Constructor`() {
        val builder = GameMapBuilder(2, 3, FLOOR)

        assertEquals(2, builder.size.x)
        assertEquals(3, builder.size.y)

        for (i in 0 until 6) {
            assertEquals(FLOOR, builder.getTerrain(i))
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
        fun `Successfully add a rectangle`() {
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
    fun `Set terrain`() {
        val builder = GameMapBuilder(2, 2, FLOOR)
            .setTerrain(1, 0, WALL)

        assertThat(builder.getTerrainList()).containsExactly(
            FLOOR, WALL,
            FLOOR, FLOOR
        )

        assertEquals(WALL, builder.getTerrain(1))
        assertEquals(WALL, builder.getTerrain(1, 0))
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
