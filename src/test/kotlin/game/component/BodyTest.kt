package game.component

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.math.rectangle.Size

class BodyTest {

    @Nested
    inner class CalculateDistanceToPosition {

        private val size = mockk<Size>()

        @Test
        fun `From simple body to target`() {
            val body = SimpleBody(4)

            every { size.getChebyshevDistance(4, 6) } returns 2

            assertThat(calculateDistanceToPosition(size, body, 6)).isEqualTo(2)

            verify(exactly = 1) { size.getChebyshevDistance(4, 6) }
            confirmVerified(size)
        }

        @Test
        fun `From big body to target`() {
            val body = BigBody(10, 2)

            every { size.getIndices(10, 2) } returns listOf(1, 2)
            every { size.getChebyshevDistance(1, 20) } returns 4
            every { size.getChebyshevDistance(2, 20) } returns 3

            assertThat(calculateDistanceToPosition(size, body, 20)).isEqualTo(3)

            verify(exactly = 1) { size.getIndices(10, 2) }
            verify(exactly = 1) { size.getChebyshevDistance(1, 20) }
            verify(exactly = 1) { size.getChebyshevDistance(2, 20) }
            confirmVerified(size)
        }

        @Test
        fun `From snake body to target`() {
            val body = SnakeBody(listOf(5, 6, 7))

            every { size.getChebyshevDistance(5, 8) } returns 9

            assertThat(calculateDistanceToPosition(size, body, 8)).isEqualTo(9)

            verify(exactly = 1) { size.getChebyshevDistance(5, 8) }
            confirmVerified(size)
        }
    }

    @Nested
    inner class CalculateDistance {

        private val size = mockk<Size>()

        @Test
        fun `From simple body to simple body`() {
            val body = SimpleBody(4)
            val target = SimpleBody(6)

            every { size.getChebyshevDistance(4, 6) } returns 2

            assertThat(calculateDistance(size, body, target)).isEqualTo(2)

            verify(exactly = 1) { size.getChebyshevDistance(4, 6) }
            confirmVerified(size)
        }

        @Test
        fun `From big body to big body`() {
            val body = BigBody(10, 2)
            val target = BigBody(20, 3)

            every { size.getIndices(10, 2) } returns listOf(1, 2)
            every { size.getIndices(20, 3) } returns listOf(4, 5)
            every { size.getChebyshevDistance(1, 4) } returns 100
            every { size.getChebyshevDistance(1, 5) } returns 101
            every { size.getChebyshevDistance(2, 4) } returns 102
            every { size.getChebyshevDistance(2, 5) } returns 103

            assertThat(calculateDistance(size, body, target)).isEqualTo(100)

            verify(exactly = 1) { size.getIndices(10, 2) }
            verify(exactly = 1) { size.getIndices(20, 3) }
            verify(exactly = 1) { size.getChebyshevDistance(1, 4) }
            verify(exactly = 1) { size.getChebyshevDistance(1, 5) }
            verify(exactly = 1) { size.getChebyshevDistance(2, 4) }
            verify(exactly = 1) { size.getChebyshevDistance(2, 5) }
            confirmVerified(size)
        }

        @Test
        fun `From simple body to snake body`() {
            val body = SimpleBody(4)
            val target = SnakeBody(listOf(1, 2))

            every { size.getChebyshevDistance(4, 1) } returns 10

            assertThat(calculateDistance(size, body, target)).isEqualTo(10)

            verify(exactly = 1) { size.getChebyshevDistance(4, 1) }
            confirmVerified(size)
        }
    }
}