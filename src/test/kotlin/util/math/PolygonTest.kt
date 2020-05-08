package util.math

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class PolygonTest {

    @Nested
    inner class CreatePolygon {

        private val p0 = Point(0.0, 1.0)
        private val p1 = Point(2.0, 3.0)
        private val p2 = Point(4.0, 5.0)
        private val p3 = Point(6.0, 7.0)

        @Test
        fun `Create triangle`() {
            assertThat(createPolygon(0.0, 1.0, 2.0, 3.0, 4.0, 5.0))
                .isEqualTo(Polygon(listOf(p0, p1, p2)))
        }

        @Test
        fun `Create quad`() {
            assertThat(createPolygon(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0))
                .isEqualTo(Polygon(listOf(p0, p1, p2, p3)))
        }

        @Test
        fun `Create triangle with too few values`() {
            assertFailsWith<IllegalArgumentException> { createPolygon(0.0, 1.0, 2.0, 3.0, 4.0) }
            assertFailsWith<IllegalArgumentException> { createPolygon(0.0, 1.0, 2.0, 3.0) }
            assertFailsWith<IllegalArgumentException> { createPolygon(0.0, 1.0, 2.0) }
            assertFailsWith<IllegalArgumentException> { createPolygon(0.0, 1.0) }
            assertFailsWith<IllegalArgumentException> { createPolygon(0.0) }
        }

        @Test
        fun `Create triangle with an odd number of values`() {
            assertFailsWith<IllegalArgumentException> { createPolygon(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0) }
        }

    }

    @Test
    fun `Rotate clockwise`() {
        val p0 = mockk<Point>()
        val p1 = mockk<Point>()
        val p2 = mockk<Point>()
        val r0 = mockk<Point>()
        val r1 = mockk<Point>()
        val r2 = mockk<Point>()

        every { p0.rotateClockwise() } returns r0
        every { p1.rotateClockwise() } returns r1
        every { p2.rotateClockwise() } returns r2

        val polygon = Polygon(listOf(p0, p1, p2))

        assertThat(polygon.rotateClockwise()).isEqualTo(Polygon(listOf(r0, r1, r2)))
    }
}