package util.ecs

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

private const val INT0 = 111
private const val INT1 = -9

private const val STRING0 = "Test"
private const val STRING1 = "Hello"

class EcsBuilderTest {

    @Nested
    inner class RegisterComponentType {

        @Test
        fun `Registering of component types`() {
            with(EcsBuilder()) {
                registerComponent<Int>()
                registerComponent<String>()
                build()
            }
        }

        @Test
        fun `Adding a component of an unregistered type fails`() {
            assertFailsWith<IllegalArgumentException> { EcsBuilder().add(42) }
        }
    }

    @Nested
    inner class BuildEntity {

        @Test
        fun `Creating an entity`() {
            val state = with(EcsBuilder()) {
                registerComponent<Int>()
                registerComponent<String>()
                add(INT0)
                add(STRING0)
                build()
            }

            assertThat(state.entities).containsOnly(0)
            assertEntity(state, 0, INT0, STRING0)
        }

        @Test
        fun `Creating 2 entities`() {
            val state = with(EcsBuilder()) {
                registerComponent<Int>()
                registerComponent<String>()
                add(INT0)
                add(STRING0)
                assertThat(buildEntity()).isSameAs(0)
                add(INT1)
                add(STRING1)
                build()
            }

            assertThat(state.entities).containsOnly(0, 1)
            assertEntity(state, 0, INT0, STRING0)
            assertEntity(state, 1, INT1, STRING1)
        }

        @Test
        fun `Skip used ids`() {
            val state = with(EcsBuilder(mutableSetOf(0, 2), mutableMapOf(), mutableMapOf())) {
                registerComponent<Int>()
                registerComponent<String>()
                add(INT0)
                add(STRING0)
                assertThat(buildEntity()).isSameAs(1)
                add(INT1)
                add(STRING1)
                build()
            }

            assertThat(state.entities).containsOnly(0, 1, 2, 3)
            assertEntity(state, 1, INT0, STRING0)
            assertEntity(state, 3, INT1, STRING1)
        }
    }

    @Nested
    inner class Data {

        @Test
        fun `Add data`() {
            val text = "Test"
            val state = with(EcsBuilder()) {
                addData(text)
                build()
            }

            assertThat(state.getData<String>()).isSameAs(text)
        }

        @Test
        fun `Get non-existing data`() {
            val state = EcsBuilder().build()

            assertFailsWith<NoSuchElementException> { state.getData<String>() }
        }
    }

    private fun assertEntity(state: EcsState, id: Int, i: Int, s: String) {
        assertThat(state.getStorage<Int>()[id]).isSameAs(i)
        assertThat(state.getStorage<String>()[id]).isSameAs(s)
    }
}