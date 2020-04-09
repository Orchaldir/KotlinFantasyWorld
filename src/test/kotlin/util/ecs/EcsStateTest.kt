package util.ecs

import assertk.assertThat
import assertk.assertions.isSameAs
import io.mockk.mockk
import org.junit.Test
import util.ecs.storage.ComponentStorage
import kotlin.test.assertNull

class ComponentMapTest {

    @Test
    fun `Test get() with existing type`() {
        val c0 = mockk<ComponentStorage<Int>>()
        val c1 = mockk<ComponentStorage<String>>()
        val state = EcsState(mapOf(Int::class to c0, String::class to c1))

        assertThat(state.get<Int>()).isSameAs(c0)
        assertThat(state.get<String>()).isSameAs(c1)
    }

    @Test
    fun `Test get() with non-existing type`() {
        val c0 = mockk<ComponentStorage<Int>>()
        val state = EcsState(mapOf(Int::class to c0))

        assertNull(state.get<String>())
    }
}