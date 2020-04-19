package game.component

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.math.Size

class BodyTest {

    @Nested
    inner class CalculateDistanceToPosition {

        private val size = mockk<Size>()

        @Test
        fun `From simple body to target`() {
            val body = SimpleBody(4)

            every { size.getManhattanDistance(4, 6) } returns 2

            assertThat(calculateDistanceToPosition(size, body, 6)).isEqualTo(2)

            verify(exactly = 1) { size.getManhattanDistance(4, 6) }
            confirmVerified(size)
        }

        @Test
        fun `From big body to target`() {
            val body = BigBody(10, 2)

            every { size.getIndices(10, 2) } returns listOf(1, 2)
            every { size.getManhattanDistance(1, 20) } returns 4
            every { size.getManhattanDistance(2, 20) } returns 3

            assertThat(calculateDistanceToPosition(size, body, 20)).isEqualTo(3)

            verify(exactly = 1) { size.getIndices(10, 2) }
            verify(exactly = 1) { size.getManhattanDistance(1, 20) }
            verify(exactly = 1) { size.getManhattanDistance(2, 20) }
            confirmVerified(size)
        }

        @Test
        fun `From snake body to target`() {
            val body = SnakeBody(listOf(5, 6, 7))

            every { size.getManhattanDistance(5, 8) } returns 9

            assertThat(calculateDistanceToPosition(size, body, 8)).isEqualTo(9)

            verify(exactly = 1) { size.getManhattanDistance(5, 8) }
            confirmVerified(size)
        }
    }
}