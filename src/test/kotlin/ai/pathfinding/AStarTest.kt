package ai.pathfinding

import ai.pathfinding.graph.OccupancyMap
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Test
import util.math.rectangle.Chebyshev
import util.math.rectangle.Size

class AStarTest {

    private val f = true
    private val o = false

    private val largeMap = listOf(
        f, f, f, f, f, f,
        f, o, o, o, o, f,
        f, f, o, f, f, f,
        f, f, f, f, f, f,
        f, f, f, f, f, f
    )
    private val largeSize = Size(6, 5)
    private val largeGraph = OccupancyMap(Chebyshev, largeMap, largeSize)

    @Test
    fun `Find a valid plan`() {
        val aStar = AStar<Boolean>()

        val path = aStar.find(largeGraph, 3, 15, 2)

        assertThat(path).isEqualTo(Path(size = 2, totalCost = 6, indices = listOf(4, 5, 11, 17, 16, 15)))
    }

    @Test
    fun `Find a valid plan to the first goal of 2`() {
        val aStar = AStar<Boolean>()

        val path = aStar.find(largeGraph, 2, setOf(13, 15), 2)

        assertThat(path).isEqualTo(Path(size = 2, totalCost = 5, indices = listOf(1, 0, 6, 12, 13)))
    }

    @Test
    fun `Find a valid plan to the second goal of 2`() {
        val aStar = AStar<Boolean>()

        val path = aStar.find(largeGraph, 5, setOf(13, 15), 2)

        assertThat(path).isEqualTo(Path(size = 2, totalCost = 4, indices = listOf(11, 17, 16, 15)))
    }

    @Test
    fun `Start & goal are the same`() {
        val values = listOf(f)
        val graph = OccupancyMap(Chebyshev, values, Size(1, 1))
        val aStar = AStar<Boolean>()

        assertThat(aStar.find(graph, 0, 0, 1)).isSameAs(GoalAlreadyReached)
    }

    @Test
    fun `Blocked by obstacle`() {
        val values = listOf(f, o, f)
        val graph = OccupancyMap(Chebyshev, values, Size(3, 1))
        val aStar = AStar<Boolean>()

        assertThat(aStar.find(graph, 0, 2, 1)).isEqualTo(NoPathFound(setOf(2), 1))
    }

    @Test
    fun `Start is an  obstacle`() {
        val values = listOf(o, f, f)
        val graph = OccupancyMap(Chebyshev, values, Size(3, 1))

        val aStar = AStar<Boolean>()

        assertThat(aStar.find(graph, 0, 2, 2)).isEqualTo(NoPathFound(setOf(2), 2))
    }

    @Test
    fun `Only goal is an  obstacle`() {
        val values = listOf(f, f, o)
        val graph = OccupancyMap(Chebyshev, values, Size(3, 1))

        val aStar = AStar<Boolean>()

        assertThat(aStar.find(graph, 0, 2, 3)).isEqualTo(NoPathFound(setOf(2), 3))
    }

    @Test
    fun `1 of 2 goals is an  obstacle`() {
        val values = listOf(f, f, f, o)
        val graph = OccupancyMap(Chebyshev, values, Size(4, 1))

        val aStar = AStar<Boolean>()

        assertThat(aStar.find(graph, 2, setOf(0, 3), 3)).isEqualTo(
            Path(
                size = 3,
                totalCost = 2,
                indices = listOf(1, 0)
            )
        )
    }

    @Test
    fun `All goals are an  obstacle`() {
        val values = listOf(o, f, o)
        val graph = OccupancyMap(Chebyshev, values, Size(3, 1))

        val aStar = AStar<Boolean>()

        assertThat(aStar.find(graph, 1, setOf(0, 2), 3)).isEqualTo(NoPathFound(setOf(0, 2), 3))
    }
}