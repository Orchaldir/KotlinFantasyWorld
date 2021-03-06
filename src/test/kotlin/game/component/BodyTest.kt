package game.component

import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.isEqualTo
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.math.Direction.*
import util.math.rectangle.Chebyshev
import util.math.rectangle.DistanceCalculator
import util.math.rectangle.Manhattan
import util.math.rectangle.Size

class BodyTest {

    @Nested
    inner class CalculateDistanceToPosition {

        private val calculator = mockk<DistanceCalculator>()
        private val size = mockk<Size>()

        @Test
        fun `From simple body to target`() {
            val body = SimpleBody(4)

            every { size.getDistance(calculator, 4, 6) } returns 2

            assertThat(calculateDistanceToPosition(calculator, size, body, 6)).isEqualTo(2)

            verify(exactly = 1) { size.getDistance(calculator, 4, 6) }
            confirmVerified(size)
        }

        @Test
        fun `From big body to target`() {
            val body = BigBody(10, 2)

            every { size.getIndices(10, 2) } returns listOf(1, 2)
            every { size.getDistance(calculator, 1, 20) } returns 4
            every { size.getDistance(calculator, 2, 20) } returns 3

            assertThat(calculateDistanceToPosition(calculator, size, body, 20)).isEqualTo(3)

            verify(exactly = 1) { size.getIndices(10, 2) }
            verify(exactly = 1) { size.getDistance(calculator, 1, 20) }
            verify(exactly = 1) { size.getDistance(calculator, 2, 20) }
            confirmVerified(size)
        }

        @Test
        fun `From snake body to target`() {
            val body = SnakeBody(listOf(5, 6, 7))

            every { size.getDistance(calculator, 5, 8) } returns 9
            every { size.getDistance(calculator, 6, 8) } returns 10
            every { size.getDistance(calculator, 7, 8) } returns 11

            assertThat(calculateDistanceToPosition(calculator, size, body, 8)).isEqualTo(9)

            verify(exactly = 1) { size.getDistance(calculator, 5, 8) }
            verify(exactly = 1) { size.getDistance(calculator, 6, 8) }
            verify(exactly = 1) { size.getDistance(calculator, 7, 8) }
            confirmVerified(size)
        }
    }

    @Nested
    inner class CalculateDistance {

        private val calculator = mockk<DistanceCalculator>()
        private val size = mockk<Size>()

        @Test
        fun `From simple body to simple body`() {
            val body = SimpleBody(4)
            val target = SimpleBody(6)

            every { size.getDistance(calculator, 4, 6) } returns 2

            assertThat(calculateDistance(calculator, size, body, target)).isEqualTo(2)

            verify(exactly = 1) { size.getDistance(calculator, 4, 6) }
            confirmVerified(size)
        }

        @Test
        fun `From big body to big body`() {
            val body = BigBody(10, 2)
            val target = BigBody(20, 3)

            every { size.getIndices(10, 2) } returns listOf(1, 2)
            every { size.getIndices(20, 3) } returns listOf(4, 5)
            every { size.getDistance(calculator, 1, 4) } returns 100
            every { size.getDistance(calculator, 1, 5) } returns 101
            every { size.getDistance(calculator, 2, 4) } returns 102
            every { size.getDistance(calculator, 2, 5) } returns 103

            assertThat(calculateDistance(calculator, size, body, target)).isEqualTo(100)

            verify(exactly = 1) { size.getIndices(10, 2) }
            verify(exactly = 1) { size.getIndices(20, 3) }
            verify(exactly = 1) { size.getDistance(calculator, 1, 4) }
            verify(exactly = 1) { size.getDistance(calculator, 1, 5) }
            verify(exactly = 1) { size.getDistance(calculator, 2, 4) }
            verify(exactly = 1) { size.getDistance(calculator, 2, 5) }
            confirmVerified(size)
        }

        @Test
        fun `From simple body to snake body`() {
            val body = SimpleBody(4)
            val target = SnakeBody(listOf(1, 2))

            every { size.getDistance(calculator, 4, 1) } returns 10
            every { size.getDistance(calculator, 4, 2) } returns 11

            assertThat(calculateDistance(calculator, size, body, target)).isEqualTo(10)

            verify(exactly = 1) { size.getDistance(calculator, 4, 1) }
            verify(exactly = 1) { size.getDistance(calculator, 4, 2) }
            confirmVerified(size)
        }
    }

    @Nested
    inner class GetPositionsAround {

        @Test
        fun `Simple around simple body at distance 1`() {
            val body = SimpleBody(4)
            val mapSize = Size(3, 3)

            assertThat(getPositionsAround(Chebyshev, mapSize, body, 1, 1))
                .containsAll(0, 1, 2, 3, 5, 6, 7, 8)
        }

        @Test
        fun `Simple around big body at distance 1`() {
            val body = BigBody(5, 2)
            val mapSize = Size(4, 4)

            assertThat(getPositionsAround(Manhattan, mapSize, body, 1, 1))
                .containsAll(1, 2, 4, 7, 8, 11, 13, 14)
        }

        @Test
        fun `Simple around snake at distance 2`() {
            val body = SnakeBody(listOf(6, 7, 8))
            val mapSize = Size(5, 3)

            assertThat(getPositionsAround(Chebyshev, mapSize, body, 1, 1))
                .containsAll(0, 1, 2, 3, 4, 5, 9, 10, 11, 13, 14)
        }

        @Test
        fun `Big around big body at distance 1`() {
            val body = BigBody(0, 2)
            val mapSize = Size(4, 4)

            assertThat(getPositionsAround(Chebyshev, mapSize, body, 2, 1))
                .containsAll(2, 6, 10, 9, 8)
        }
    }

    @Nested
    inner class UpdateBody {

        @Test
        fun `Update a simple body`() {
            val body = SimpleBody(4, NORTH)

            assertThat(updateBody(body, 1, SOUTH)).isEqualTo(SimpleBody(1, SOUTH))
        }

        @Test
        fun `Update a big body`() {
            val body = BigBody(4, 2, WEST)
            val newBody = updateBody(body, 1, EAST)

            assertThat(newBody).isEqualTo(BigBody(1, 2, EAST))
        }

        @Test
        fun `Update a snake body`() {
            val body = SnakeBody(listOf(4, 3, 6))
            val newBody = updateBody(body, 1, WEST)

            assertThat(newBody).isEqualTo(SnakeBody(listOf(1, 4, 3), WEST))
        }
    }
}