package game.map

import game.map.Terrain.FLOOR
import game.map.Terrain.WALL
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val ENTITY0 = 1
private const val ENTITY1 = 2

class GameMapTest {

    @Test
    fun `Cell is walkable based on terrain`() {
        val map = GameMapBuilder(2, 1, FLOOR)
            .setTerrain(1, 0, WALL)
            .build()

        assertTrue(map.isWalkable(0, ENTITY0))
        assertFalse(map.isWalkable(1, ENTITY0))
    }

    @Test
    fun `Index is walkable with size 2`() {
        val map = GameMapBuilder(3, 3, FLOOR)
            .setTerrain(0, 0, WALL)
            .build()

        assertFalse(map.isWalkable(index = 0, size = 2, entity = ENTITY0))
        assertTrue(map.isWalkable(index = 1, size = 2, entity = ENTITY0))
        assertTrue(map.isWalkable(index = 3, size = 2, entity = ENTITY0))
        assertTrue(map.isWalkable(index = 4, size = 2, entity = ENTITY0))
    }

    @Test
    fun `Index is outside with size 2`() {
        val map = GameMapBuilder(3, 3, FLOOR)
            .setTerrain(0, 0, WALL)
            .build()

        assertFalse(map.isWalkable(index = 2, size = 2, entity = ENTITY0))
        assertFalse(map.isWalkable(index = 5, size = 2, entity = ENTITY0))
        assertFalse(map.isWalkable(index = 6, size = 2, entity = ENTITY0))
        assertFalse(map.isWalkable(index = 7, size = 2, entity = ENTITY0))
        assertFalse(map.isWalkable(index = 8, size = 2, entity = ENTITY0))
    }

    @Test
    fun `Entity can walk in its own cell`() {
        val map = GameMapBuilder(2, 1, FLOOR)
            .setEntity(0, ENTITY0)
            .build()

        assertTrue(map.isWalkable(0, ENTITY0))
    }

    @Test
    fun `Entity is blocked by another entity`() {
        val map = GameMapBuilder(2, 1, FLOOR)
            .setEntity(0, ENTITY0)
            .build()

        assertFalse(map.isWalkable(0, ENTITY1))
    }

}
