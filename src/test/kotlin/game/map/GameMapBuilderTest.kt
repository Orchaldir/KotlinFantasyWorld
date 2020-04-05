package game.map

import assertk.assertThat
import assertk.assertions.containsExactly
import game.map.Terrain.FLOOR
import game.map.Terrain.WALL
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GameMapBuilderTest {

    @Test
    fun testConstructor() {
        val builder = GameMapBuilder(2, 3, FLOOR)

        assertEquals(2, builder.size.x)
        assertEquals(3, builder.size.y)

        for (i in 0 until 6) {
            assertEquals(FLOOR, builder.getTerrain(i))
        }
    }

    @Test
    fun testAddBorder() {
        val builder = GameMapBuilder(3, 4, FLOOR)
            .addBorder(WALL)

        assertThat(builder.getTerrainList()).containsExactly(
            WALL, WALL, WALL,
            WALL, FLOOR, WALL,
            WALL, FLOOR, WALL,
            WALL, WALL, WALL
        )
    }


    @Test
    fun testAddRectangle() {
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
    fun testAddRectangleWithInvalidStart() {
        val builder = GameMapBuilder(4, 5, FLOOR)

        assertFailsWith<IllegalArgumentException> { builder.addRectangle(-1, 0, 3, 4, WALL) }
        assertFailsWith<IllegalArgumentException> { builder.addRectangle(0, -1, 3, 4, WALL) }
    }

    @Test
    fun testAddRectangleWithInvalidSize() {
        val builder = GameMapBuilder(4, 5, FLOOR)

        assertFailsWith<IllegalArgumentException> { builder.addRectangle(0, 0, -1, 4, WALL) }
        assertFailsWith<IllegalArgumentException> { builder.addRectangle(0, 0, 3, -1, WALL) }
    }

    @Test
    fun testAddRectangleReachesOutside() {
        val builder = GameMapBuilder(4, 5, FLOOR)

        assertFailsWith<IllegalArgumentException> { builder.addRectangle(1, 1, 4, 4, WALL) }
        assertFailsWith<IllegalArgumentException> { builder.addRectangle(1, 1, 3, 5, WALL) }
    }

}
