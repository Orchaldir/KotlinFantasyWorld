package util.math.rectangle

import assertk.assertThat
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DistanceCalculatorTest {

    @Nested
    inner class Chebyshev {

        @Test
        fun `Test distance 0`() {
            assertThat(calculateDistance(Chebyshev, 2, 2, 2, 2)).isSameAs(0)
        }

        @Test
        fun `Test distance 1`() {
            assertThat(calculateDistance(Chebyshev, 2, 2, 1, 1)).isSameAs(1)
            assertThat(calculateDistance(Chebyshev, 2, 2, 2, 1)).isSameAs(1)
            assertThat(calculateDistance(Chebyshev, 2, 2, 3, 1)).isSameAs(1)
            assertThat(calculateDistance(Chebyshev, 2, 2, 1, 2)).isSameAs(1)
            assertThat(calculateDistance(Chebyshev, 2, 2, 3, 2)).isSameAs(1)
            assertThat(calculateDistance(Chebyshev, 2, 2, 1, 3)).isSameAs(1)
            assertThat(calculateDistance(Chebyshev, 2, 2, 2, 3)).isSameAs(1)
            assertThat(calculateDistance(Chebyshev, 2, 2, 3, 3)).isSameAs(1)
        }

        @Test
        fun `Test distance 2`() {
            assertThat(calculateDistance(Chebyshev, 2, 2, 0, 0)).isSameAs(2)
            assertThat(calculateDistance(Chebyshev, 2, 2, 2, 0)).isSameAs(2)
            assertThat(calculateDistance(Chebyshev, 2, 2, 4, 0)).isSameAs(2)
            assertThat(calculateDistance(Chebyshev, 2, 2, 0, 2)).isSameAs(2)
            assertThat(calculateDistance(Chebyshev, 2, 2, 4, 2)).isSameAs(2)
            assertThat(calculateDistance(Chebyshev, 2, 2, 0, 4)).isSameAs(2)
            assertThat(calculateDistance(Chebyshev, 2, 2, 2, 4)).isSameAs(2)
            assertThat(calculateDistance(Chebyshev, 2, 2, 4, 4)).isSameAs(2)
        }
    }

    @Nested
    inner class Manhattan {

        @Test
        fun `Test distance 0`() {
            assertThat(calculateDistance(Manhattan, 1, 1, 1, 1)).isSameAs(0)
        }

        @Test
        fun `Test distance 1`() {
            assertThat(calculateDistance(Manhattan, 1, 1, 1, 0)).isSameAs(1)
            assertThat(calculateDistance(Manhattan, 1, 1, 0, 1)).isSameAs(1)
            assertThat(calculateDistance(Manhattan, 1, 1, 2, 1)).isSameAs(1)
            assertThat(calculateDistance(Manhattan, 1, 1, 1, 2)).isSameAs(1)
        }

        @Test
        fun `Test distance 2`() {
            assertThat(calculateDistance(Manhattan, 1, 1, 0, 0)).isSameAs(2)
            assertThat(calculateDistance(Manhattan, 1, 1, 2, 0)).isSameAs(2)
            assertThat(calculateDistance(Manhattan, 1, 1, 0, 2)).isSameAs(2)
            assertThat(calculateDistance(Manhattan, 1, 1, 2, 2)).isSameAs(2)
        }
    }

}