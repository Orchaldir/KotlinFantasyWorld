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
    fun `Entity can walk in its own cell`() {
        val map = GameMapBuilder(2, 1, FLOOR)
            .setEntity(0, 0, ENTITY0)
            .build()

        assertTrue(map.isWalkable(0, ENTITY0))
    }

    @Test
    fun `Entity is blocked by another entity`() {
        val map = GameMapBuilder(2, 1, FLOOR)
            .setEntity(0, 0, ENTITY0)
            .build()

        assertFalse(map.isWalkable(0, ENTITY1))
    }

}
