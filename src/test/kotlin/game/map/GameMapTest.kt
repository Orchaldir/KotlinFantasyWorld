package game.map

import assertk.assertThat
import assertk.assertions.isEqualTo
import game.map.Terrain.FLOOR
import game.map.Terrain.WALL
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.math.rectangle.Size
import kotlin.test.assertNull

private const val ENTITY0 = 1
private const val ENTITY1 = 2

class GameMapTest {

    @Nested
    inner class GetEntity {

        @Test
        fun `No entity to get`() {
            val map = GameMapBuilder(2, 1, FLOOR).build()

            assertNull(map.getEntity(0, 0))
            assertNull(map.getEntity(1, 0))
        }

        @Test
        fun `Get entity`() {
            val map = GameMapBuilder(2, 1, FLOOR)
                .setEntity(0, 42)
                .build()

            assertThat(map.getEntity(0, 0)).isEqualTo(42)
            assertNull(map.getEntity(1, 0))
        }
    }

    @Nested
    inner class CreateOccupancyMap {

        @Test
        fun `Create occupancy map from terrain`() {
            val map = GameMapBuilder(2, 3, FLOOR)
                .setTerrain(1, 0, WALL)
                .build()

            val occupancyMap = map.createOccupancyMap(0)

            assertThat(occupancyMap.list).isEqualTo(
                listOf(
                    true, false,
                    true, true,
                    true, true
                )
            )
            assertThat(occupancyMap.size).isEqualTo(Size(2, 3))
        }

    }

    @Nested
    inner class CheckWalkability {

        @Test
        fun `Cell is walkable based on terrain`() {
            val map = GameMapBuilder(2, 1, FLOOR)
                .setTerrain(1, 0, WALL)
                .build()

            assertThat(map.checkWalkability(0, ENTITY0)).isEqualTo(Walkable(0))
            assertThat(map.checkWalkability(1, ENTITY0)).isEqualTo(BlockedByObstacle)
        }

        @Test
        fun `Index is walkable with size 2`() {
            val map = GameMapBuilder(3, 3, FLOOR)
                .setTerrain(0, 0, WALL)
                .build()

            assertThat(map.checkWalkability(position = 0, size = 2, entity = ENTITY0)).isEqualTo(BlockedByObstacle)
            assertThat(map.checkWalkability(position = 1, size = 2, entity = ENTITY0)).isEqualTo(Walkable(1))
            assertThat(map.checkWalkability(position = 3, size = 2, entity = ENTITY0)).isEqualTo(Walkable(3))
            assertThat(map.checkWalkability(position = 4, size = 2, entity = ENTITY0)).isEqualTo(Walkable(4))
        }

        @Test
        fun `Index is outside`() {
            val map = GameMapBuilder(3, 3, FLOOR).build()

            assertThat(map.checkWalkability(-1, ENTITY0)).isEqualTo(OutsideMap)
            assertThat(map.checkWalkability(9, ENTITY0)).isEqualTo(OutsideMap)
        }

        @Test
        fun `Index is outside with size 2`() {
            val map = GameMapBuilder(3, 3, FLOOR).build()

            assertThat(map.checkWalkability(position = 2, size = 2, entity = ENTITY0)).isEqualTo(OutsideMap)
            assertThat(map.checkWalkability(position = 5, size = 2, entity = ENTITY0)).isEqualTo(OutsideMap)
            assertThat(map.checkWalkability(position = 6, size = 2, entity = ENTITY0)).isEqualTo(OutsideMap)
            assertThat(map.checkWalkability(position = 7, size = 2, entity = ENTITY0)).isEqualTo(OutsideMap)
            assertThat(map.checkWalkability(position = 8, size = 2, entity = ENTITY0)).isEqualTo(OutsideMap)
        }

        @Test
        fun `Entity can walk in its own cell`() {
            val map = GameMapBuilder(2, 1, FLOOR)
                .setEntity(0, ENTITY0)
                .build()

            assertThat(map.checkWalkability(0, ENTITY0)).isEqualTo(Walkable(0))
        }

        @Test
        fun `Entity is blocked by another entity`() {
            val map = GameMapBuilder(2, 1, FLOOR)
                .setEntity(0, ENTITY0)
                .build()

            assertThat(map.checkWalkability(0, ENTITY1)).isEqualTo(BlockedByEntity(ENTITY0))
        }
    }

}
