package util.ecs

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isSameAs
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

private const val INT0 = 111
private const val INT1 = -9

private const val STRING0 = "Test"
private const val STRING1 = "Hello"

class EcsBuilderTest {

    @Test
    fun `Registering of component types`() {
        val state = EcsBuilder()
            .register<Int>()
            .register<String>()
            .build()

        assertNotNull(state.get<Int>())
        assertNotNull(state.get<String>())
    }

    @Test
    fun `Adding a component of an unregistered type fails`() {
        assertFailsWith<IllegalArgumentException> { EcsBuilder().add(42) }
    }

    @Test
    fun `Creating an entity`() {
        val state = EcsBuilder()
            .register<Int>()
            .register<String>()
            .add(INT0)
            .add(STRING0)
            .build()

        assertThat(state.entityIds).containsOnly(0)
        assertEntity(state, 0, INT0, STRING0)
    }

    @Test
    fun `Creating 2 entities`() {
        val state = EcsBuilder()
            .register<Int>()
            .register<String>()
            .add(INT0)
            .add(STRING0)
            .buildEntity()
            .add(INT1)
            .add(STRING1)
            .build()

        assertThat(state.entityIds).containsOnly(0, 1)
        assertEntity(state, 0, INT0, STRING0)
        assertEntity(state, 1, INT1, STRING1)
    }

    @Test
    fun `Skip used ids`() {
        val state = EcsBuilder(mutableSetOf(0, 2), mutableMapOf())
            .register<Int>()
            .register<String>()
            .add(INT0)
            .add(STRING0)
            .buildEntity()
            .add(INT1)
            .add(STRING1)
            .build()

        assertThat(state.entityIds).containsOnly(0, 1, 2, 3)
        assertEntity(state, 1, INT0, STRING0)
        assertEntity(state, 3, INT1, STRING1)
    }

    private fun assertEntity(state: EcsState, id: Int, i: Int, s: String) {
        assertThat(state.get<Int>()?.get(id)).isSameAs(i)
        assertThat(state.get<String>()?.get(id)).isSameAs(s)
    }
}