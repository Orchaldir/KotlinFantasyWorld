package game.map

import game.map.Terrain.FLOOR
import game.map.Terrain.WALL
import kotlin.test.Test
import kotlin.test.assertEquals

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

        assertEquals(WALL, builder.getTerrain(0, 0))
        assertEquals(WALL, builder.getTerrain(1, 0))
        assertEquals(WALL, builder.getTerrain(2, 0))

        assertEquals(WALL, builder.getTerrain(0, 1))
        assertEquals(FLOOR, builder.getTerrain(1, 1))
        assertEquals(WALL, builder.getTerrain(2, 1))

        assertEquals(WALL, builder.getTerrain(0, 2))
        assertEquals(FLOOR, builder.getTerrain(1, 2))
        assertEquals(WALL, builder.getTerrain(2, 2))

        assertEquals(WALL, builder.getTerrain(0, 3))
        assertEquals(WALL, builder.getTerrain(1, 3))
        assertEquals(WALL, builder.getTerrain(2, 3))
    }


    @Test
    fun testAddRectangle() {
        val builder = GameMapBuilder(4, 5, FLOOR)
            .addRectangle(0, 0, 3, 4, WALL)

        assertEquals(WALL, builder.getTerrain(0, 0))
        assertEquals(WALL, builder.getTerrain(1, 0))
        assertEquals(WALL, builder.getTerrain(2, 0))
        assertEquals(FLOOR, builder.getTerrain(3, 0))

        assertEquals(WALL, builder.getTerrain(0, 1))
        assertEquals(FLOOR, builder.getTerrain(1, 1))
        assertEquals(WALL, builder.getTerrain(2, 1))
        assertEquals(FLOOR, builder.getTerrain(3, 1))

        assertEquals(WALL, builder.getTerrain(0, 2))
        assertEquals(FLOOR, builder.getTerrain(1, 2))
        assertEquals(WALL, builder.getTerrain(2, 2))
        assertEquals(FLOOR, builder.getTerrain(3, 2))

        assertEquals(WALL, builder.getTerrain(0, 3))
        assertEquals(WALL, builder.getTerrain(1, 3))
        assertEquals(WALL, builder.getTerrain(2, 3))
        assertEquals(FLOOR, builder.getTerrain(3, 3))

        assertEquals(FLOOR, builder.getTerrain(0, 4))
        assertEquals(FLOOR, builder.getTerrain(1, 4))
        assertEquals(FLOOR, builder.getTerrain(2, 4))
        assertEquals(FLOOR, builder.getTerrain(3, 4))
    }

}
