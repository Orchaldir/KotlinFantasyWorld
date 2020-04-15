package game.map

import assertk.assertThat
import assertk.assertions.isEqualTo
import game.map.Terrain.FLOOR
import game.map.Terrain.WALL
import game.map.Walkability.*
import org.junit.jupiter.api.Test

private const val ENTITY0 = 1
private const val ENTITY1 = 2

class GameMapTest {

    @Test
    fun `Cell is walkable based on terrain`() {
        val map = GameMapBuilder(2, 1, FLOOR)
            .setTerrain(1, 0, WALL)
            .build()

        assertThat(map.checkWalkability(0, ENTITY0)).isEqualTo(WALKABLE)
        assertThat(map.checkWalkability(1, ENTITY0)).isEqualTo(BLOCKED_BY_OBSTACLE)
    }

    @Test
    fun `Index is walkable with size 2`() {
        val map = GameMapBuilder(3, 3, FLOOR)
            .setTerrain(0, 0, WALL)
            .build()

        assertThat(map.checkWalkability(index = 0, size = 2, entity = ENTITY0)).isEqualTo(BLOCKED_BY_OBSTACLE)
        assertThat(map.checkWalkability(index = 1, size = 2, entity = ENTITY0)).isEqualTo(WALKABLE)
        assertThat(map.checkWalkability(index = 3, size = 2, entity = ENTITY0)).isEqualTo(WALKABLE)
        assertThat(map.checkWalkability(index = 4, size = 2, entity = ENTITY0)).isEqualTo(WALKABLE)
    }

    @Test
    fun `Index is outside`() {
        val map = GameMapBuilder(3, 3, FLOOR).build()

        assertThat(map.checkWalkability(-1, ENTITY0)).isEqualTo(OUTSIDE_MAP)
        assertThat(map.checkWalkability(9, ENTITY0)).isEqualTo(OUTSIDE_MAP)
    }

    @Test
    fun `Index is outside with size 2`() {
        val map = GameMapBuilder(3, 3, FLOOR).build()

        assertThat(map.checkWalkability(index = 2, size = 2, entity = ENTITY0)).isEqualTo(OUTSIDE_MAP)
        assertThat(map.checkWalkability(index = 5, size = 2, entity = ENTITY0)).isEqualTo(OUTSIDE_MAP)
        assertThat(map.checkWalkability(index = 6, size = 2, entity = ENTITY0)).isEqualTo(OUTSIDE_MAP)
        assertThat(map.checkWalkability(index = 7, size = 2, entity = ENTITY0)).isEqualTo(OUTSIDE_MAP)
        assertThat(map.checkWalkability(index = 8, size = 2, entity = ENTITY0)).isEqualTo(OUTSIDE_MAP)
    }

    @Test
    fun `Entity can walk in its own cell`() {
        val map = GameMapBuilder(2, 1, FLOOR)
            .setEntity(0, ENTITY0)
            .build()

        assertThat(map.checkWalkability(0, ENTITY0)).isEqualTo(WALKABLE)
    }

    @Test
    fun `Entity is blocked by another entity`() {
        val map = GameMapBuilder(2, 1, FLOOR)
            .setEntity(0, ENTITY0)
            .build()

        assertThat(map.checkWalkability(0, ENTITY1)).isEqualTo(BLOCKED_BY_ENTITY)
    }

}
