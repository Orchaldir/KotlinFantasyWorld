package util.ecs.storage

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val TYPE = "Type"

private const val ID0 = 0
private const val ID1 = 9
private const val ID2 = 42
private const val ID3 = 99

private const val A = "A"
private const val B = "B"
private const val C = "C"
private const val D = "D"
private const val E = "E"

private const val NO_ID = 1

class ComponentMapTest {

    private val components: Map<Int, String> = mapOf(ID0 to A, ID1 to B, ID2 to C)

    @Nested
    inner class Has {

        @Test
        fun `Test has() with known id`() {
            val storage = ComponentMap(TYPE, components)

            assertTrue(storage.has(ID0))
            assertTrue(storage.has(ID1))
            assertTrue(storage.has(ID2))
        }

        @Test
        fun `Test has() with unknown id`() {
            val storage = ComponentMap(TYPE, components)

            assertFalse(storage.has(NO_ID))
        }

    }

    @Nested
    inner class Get {

        @Test
        fun `Test get() with known id`() {
            val storage = ComponentMap(TYPE, components)

            assertThat(storage[ID0]).isSameAs(A)
            assertThat(storage[ID1]).isSameAs(B)
            assertThat(storage[ID2]).isSameAs(C)
        }

        @Test
        fun `Test get() with unknown id`() {
            val storage = ComponentMap(TYPE, components)

            assertNull(storage[NO_ID])
        }

    }

    @Nested
    inner class GetOrThrow {

        @Test
        fun `Test getOrThrow() with known id`() {
            val storage = ComponentMap(TYPE, components)

            assertThat(storage.getOrThrow(ID0)).isSameAs(A)
            assertThat(storage.getOrThrow(ID1)).isSameAs(B)
            assertThat(storage.getOrThrow(ID2)).isSameAs(C)
        }

        @Test
        fun `Test getOrThrow() with unknown id`() {
            val storage = ComponentMap(TYPE, components)

            assertFailsWith<NoSuchElementException> { storage.getOrThrow(NO_ID) }
        }

    }

    @Test
    fun `Get all components`() {
        val storage = ComponentMap(TYPE, components)

        assertThat(storage.getAll()).containsOnly(A, B, C)
    }

    @Test
    fun `Get the ids of all entities with this component`() {
        val storage = ComponentMap(TYPE, components)

        assertThat(storage.getIds()).containsOnly(ID0, ID1, ID2)
    }

    @Nested
    inner class UpdateAndRemove {

        @Test
        fun `Update and remove some components`() {
            val storage = ComponentMap(TYPE, components)
            val copy = storage.updateAndRemove(mapOf(ID0 to D, ID3 to E), setOf(ID2))

            assertThat(copy[ID0]).isSameAs(D)
            assertThat(copy[ID1]).isSameAs(B)
            assertNull(copy[ID2])
            assertThat(copy[ID3]).isSameAs(E)
        }

        @Test
        fun `Only update some components`() {
            val storage = ComponentMap(TYPE, components)
            val copy = storage.updateAndRemove(mapOf(ID0 to D, ID3 to E))

            assertThat(copy[ID0]).isSameAs(D)
            assertThat(copy[ID1]).isSameAs(B)
            assertThat(copy[ID2]).isSameAs(C)
            assertThat(copy[ID3]).isSameAs(E)
        }

    }

}
