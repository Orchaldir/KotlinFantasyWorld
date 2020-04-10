package util.math

import assertk.assertThat
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val size = Size(2, 3)

class SizeTest {

    @Test
    fun `Test Constructor`() {
        assertEquals(2, size.x)
        assertEquals(3, size.y)
    }

    @Test
    fun `Test number of cells`() {
        assertEquals(6, size.getCells())
    }

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

    @Test
    fun `Get x from index`() {
        assertThat(size.getX(0)).isSameAs(0)
        assertThat(size.getX(1)).isSameAs(1)
        assertThat(size.getX(2)).isSameAs(0)
        assertThat(size.getX(3)).isSameAs(1)
        assertThat(size.getX(4)).isSameAs(0)
        assertThat(size.getX(5)).isSameAs(1)
    }

    @Test
    fun `Get x from index outside area fails`() {
        assertFailsWith<IllegalArgumentException> { size.getX(-1) }
        assertFailsWith<IllegalArgumentException> { size.getX(6) }
    }

    @Test
    fun `Get y from index`() {
        assertThat(size.getY(0)).isSameAs(0)
        assertThat(size.getY(1)).isSameAs(0)
        assertThat(size.getY(2)).isSameAs(1)
        assertThat(size.getY(3)).isSameAs(1)
        assertThat(size.getY(4)).isSameAs(2)
        assertThat(size.getY(5)).isSameAs(2)
    }

    @Test
    fun `Get y from index outside area fails`() {
        assertFailsWith<IllegalArgumentException> { size.getY(-1) }
        assertFailsWith<IllegalArgumentException> { size.getY(6) }
    }
}
