package util.ecs

import assertk.assertThat
import assertk.assertions.isSameAs
import io.mockk.mockk
import org.junit.Test
import util.ecs.storage.ComponentStorage

class ComponentMapTest {

    @Test
    fun `Test get() with known id`() {
        val c0 = mockk<ComponentStorage<Int>>()
        val state = EcsState(mapOf(0 to c0))

        assertThat(state.get<Int>(0)).isSameAs(c0)
    }
}