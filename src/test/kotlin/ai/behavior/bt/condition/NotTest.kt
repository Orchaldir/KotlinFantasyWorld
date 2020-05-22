package ai.behavior.bt.condition

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class NotTest {

    private val c0 = mockk<Condition<Int>>()

    private val not = Not(c0)

    private val name = "test0"
    private val state = 42

    @Test
    fun `Test name`() {
        every { c0.getName() } returns name

        assertThat(not.getName()).isEqualTo("!test0")
    }

    @Test
    fun `Base condition is true`() {
        every { c0.check(state) } returns true

        assertThat(not.check(state)).isSameAs(false)
    }

    @Test
    fun `Base condition is false`() {
        every { c0.check(state) } returns false

        assertThat(not.check(state)).isSameAs(true)
    }
}