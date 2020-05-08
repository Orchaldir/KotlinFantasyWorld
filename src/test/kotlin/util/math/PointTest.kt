package util.math

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class PointTest {

    @Nested
    inner class RotateClockwise {
        @Test
        fun `Rotate clockwise`() {
            assertThat(Point(2.0, 0.3).rotateClockwise()).isEqualTo(Point(0.3, -2.0))
        }

        @Test
        fun `Rotate zero clockwise `() {
            assertThat(Point(0.0, 0.0).rotateClockwise()).isEqualTo(Point(0.0, -0.0))
        }
    }

}