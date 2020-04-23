package ai.pathfinding

import ai.pathfinding.graph.OccupancyMap
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Test
import util.math.Size
import kotlin.test.fail

class AStarTest {

    private val f = true
    private val o = false

    @Test
    fun `Find a valid plan`() {
        val values = mutableListOf(
            f, f, f, f, f, f,
            f, o, o, o, o, f,
            f, f, o, f, f, f,
            f, f, f, f, f, f,
            f, f, f, f, f, f
        )
        val graph = OccupancyMap(values, Size(6, 5))
        val aStar = AStar<Boolean>()

        val path = aStar.find(graph, 3, 15)

        if (path is Path) {
            assertThat(path.indices).containsExactly(3, 4, 5, 11, 17, 16, 15)
        } else {
            fail("Not a valid plan!")
        }
    }

    @Test
    fun `Blocked by obstacle`() {
        val values = mutableListOf(f, o, f)
        val graph = OccupancyMap(values, Size(3, 1))
        val aStar = AStar<Boolean>()

        assertThat(aStar.find(graph, 0, 2)).isSameAs(NoPath)
    }

    @Test
    fun `Start is an  obstacle`() {
        val values = mutableListOf(o, f, f)
        val graph = OccupancyMap(values, Size(3, 1))

        val aStar = AStar<Boolean>()

        assertThat(aStar.find(graph, 0, 2)).isSameAs(NoPath)
    }

    @Test
    fun `End is an  obstacle`() {
        val values = mutableListOf(f, f, o)
        val graph = OccupancyMap(values, Size(3, 1))

        val aStar = AStar<Boolean>()

        assertThat(aStar.find(graph, 0, 2)).isSameAs(NoPath)
    }
}