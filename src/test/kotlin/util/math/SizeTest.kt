package util.math

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val size = Size(2, 3)

class SizeTest {

    @Test
    fun testConstructor() {
        assertEquals(2, size.x)
        assertEquals(3, size.y)
    }

    @Test
    fun testGetCells() {
        assertEquals(6, size.getCells())
    }

    @Test
    fun testGetIndex() {
        assertEquals(0, size.getIndex(0, 0))
        assertEquals(1, size.getIndex(1, 0))
        assertEquals(2, size.getIndex(0, 1))
        assertEquals(3, size.getIndex(1, 1))
        assertEquals(4, size.getIndex(0, 2))
        assertEquals(5, size.getIndex(1, 2))
    }

    @Test
    fun testGetIndexOutside() {
        assertFailsWith<IllegalArgumentException> { size.getIndex(-1, 0) }
        assertFailsWith<IllegalArgumentException> { size.getIndex(0, -1) }
        assertFailsWith<IllegalArgumentException> { size.getIndex(2, 0) }
        assertFailsWith<IllegalArgumentException> { size.getIndex(0, 3) }
    }
}
