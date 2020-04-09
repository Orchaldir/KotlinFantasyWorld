package util.ecs.storage

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isSameAs
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val ID0 = 0
private const val ID1 = 9
private const val ID2 = 42

private const val A = "A"
private const val B = "B"
private const val C = "C"

private const val NO_ID = 1

class ComponentMapTest {

    private val components: Map<Int, String> = mapOf(ID0 to A, ID1 to B, ID2 to C)

    @Test
    fun `Test has() with known id`() {
        val storage = ComponentMap(components)

        assertTrue(storage.has(ID0))
        assertTrue(storage.has(ID1))
        assertTrue(storage.has(ID2))
    }

    @Test
    fun `Test has() with unknown id`() {
        val storage = ComponentMap(components)

        assertFalse(storage.has(NO_ID))
    }

    @Test
    fun `Test get() with known id`() {
        val storage = ComponentMap(components)

        assertThat(storage[ID0]).isSameAs(A)
        assertThat(storage[ID1]).isSameAs(B)
        assertThat(storage[ID2]).isSameAs(C)
    }

    @Test
    fun `Test get() with unknown id`() {
        val storage = ComponentMap(components)

        assertNull(storage[NO_ID])
    }

    @Test
    fun `Test getAll()`() {
        val storage = ComponentMap(components)

        assertThat(storage.getAll()).containsOnly(A, B, C)
    }

    @Test
    fun `Test getIds()`() {
        val storage = ComponentMap(components)

        assertThat(storage.getIds()).containsOnly(ID0, ID1, ID2)
    }

}