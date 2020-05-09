package util.math.rectangle

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.math.Direction
import kotlin.test.*

private val size = Size(2, 3)

class SizeTest {

    @Test
    fun `Test Constructor`() {
        assertEquals(2, size.x)
        assertEquals(3, size.y)
    }

    @Test
    fun `Test number of cells`() {
        assertEquals(6, size.cells)
    }

    @Nested
    inner class GetIndex {
        @Test
        fun `Convert x & y to index`() {
            assertEquals(0, size.getIndex(0, 0))
            assertEquals(1, size.getIndex(1, 0))
            assertEquals(2, size.getIndex(0, 1))
            assertEquals(3, size.getIndex(1, 1))
            assertEquals(4, size.getIndex(0, 2))
            assertEquals(5, size.getIndex(1, 2))
        }

        @Test
        fun `Convert x & y outside the area to index fails`() {
            assertFailsWith<IllegalArgumentException> { size.getIndex(-1, 0) }
            assertFailsWith<IllegalArgumentException> { size.getIndex(0, -1) }
            assertFailsWith<IllegalArgumentException> { size.getIndex(2, 0) }
            assertFailsWith<IllegalArgumentException> { size.getIndex(0, 3) }
        }
    }


    @Nested
    inner class GetIndices {

        private val size = Size(3, 4)

        @Test
        fun `Get indices with size 2`() {
            assertThat(size.getIndices(0, 2)).containsExactly(0, 1, 3, 4)
            assertThat(size.getIndices(4, 2)).containsExactly(4, 5, 7, 8)
        }

        @Test
        fun `Get indices with start outside`() {
            assertThat(size.getIndices(-1, 3)).isEmpty()
        }

        @Test
        fun `Get indices with end outside`() {
            assertThat(size.getIndices(1, 3)).isEmpty()
        }
    }

    @Nested
    inner class GetX {
        @Test
        fun `Get x from index`() {
            assertThat(size.getX(0)).isSameAs(0)
            assertThat(size.getX(1)).isSameAs(1)
            assertThat(size.getX(2)).isSameAs(0)
            assertThat(size.getX(3)).isSameAs(1)
            assertThat(size.getX(4)).isSameAs(0)
            assertThat(size.getX(5)).isSameAs(1)
        }
    }

    @Nested
    inner class GetY {
        @Test
        fun `Get y from index`() {
            assertThat(size.getY(0)).isSameAs(0)
            assertThat(size.getY(1)).isSameAs(0)
            assertThat(size.getY(2)).isSameAs(1)
            assertThat(size.getY(3)).isSameAs(1)
            assertThat(size.getY(4)).isSameAs(2)
            assertThat(size.getY(5)).isSameAs(2)
        }
    }

    @Nested
    inner class GetNeighbor {

        private val size = Size(3, 3)

        @Test
        fun `Get each neighbors`() {
            assertThat(size.getNeighbor(4, Direction.NORTH)).isSameAs(1)
            assertThat(size.getNeighbor(4, Direction.EAST)).isSameAs(5)
            assertThat(size.getNeighbor(4, Direction.SOUTH)).isSameAs(7)
            assertThat(size.getNeighbor(4, Direction.WEST)).isSameAs(3)
        }

        @Test
        fun `No neighbor outside map`() {
            assertNull(size.getNeighbor(1, Direction.NORTH))
            assertNull(size.getNeighbor(5, Direction.EAST))
            assertNull(size.getNeighbor(7, Direction.SOUTH))
            assertNull(size.getNeighbor(3, Direction.WEST))
        }
    }

    // inside check

    @Nested
    inner class IsInsideForX {

        @Test
        fun `x is inside`() {
            assertTrue(size.isInsideForX(0))
            assertTrue(size.isInsideForX(1))
        }

        @Test
        fun `x is outside`() {
            assertFalse(size.isInsideForX(-1))
            assertFalse(size.isInsideForX(2))
        }

    }

    @Nested
    inner class IsInsideForY {

        @Test
        fun `y is inside`() {
            assertTrue(size.isInsideForY(0))
            assertTrue(size.isInsideForY(1))
            assertTrue(size.isInsideForY(2))
        }

        @Test
        fun `y is outside`() {
            assertFalse(size.isInsideForY(-1))
            assertFalse(size.isInsideForY(3))
        }

    }

    @Nested
    inner class IsInside {

        @Test
        fun `Index is inside`() {
            assertTrue(size.isInside(0))
            assertTrue(size.isInside(1))
            assertTrue(size.isInside(2))
            assertTrue(size.isInside(3))
            assertTrue(size.isInside(4))
            assertTrue(size.isInside(5))
        }

        @Test
        fun `x & y are inside`() {
            assertTrue(size.isInside(0, 0))
            assertTrue(size.isInside(1, 0))
            assertTrue(size.isInside(0, 1))
            assertTrue(size.isInside(1, 1))
            assertTrue(size.isInside(0, 2))
            assertTrue(size.isInside(1, 2))
        }

        @Test
        fun `Index is outside`() {
            assertFalse(size.isInside(-1))
            assertFalse(size.isInside(6))
        }

        @Test
        fun `x & y are outside`() {
            assertFalse(size.isInside(-1, 0))
            assertFalse(size.isInside(3, 0))
            assertFalse(size.isInside(0, -1))
            assertFalse(size.isInside(0, 4))
        }

    }

    @Nested
    inner class IsRectangleInside {

        private val size = Size(3, 4)

        @Test
        fun `Area is inside`() {
            assertTrue(size.isAreaInside(0, 2))
            assertTrue(size.isAreaInside(4, 2))
        }

        @Test
        fun `Index is outside`() {
            assertFalse(size.isAreaInside(-1, 2))
            assertFalse(size.isAreaInside(12, 2))
        }
    }
}
