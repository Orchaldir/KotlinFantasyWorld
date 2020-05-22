package ai.behavior.bt.condition

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class AndTest {

    private val c0 = mockk<Condition<Int>>()
    private val c1 = mockk<Condition<Int>>()
    private val c2 = mockk<Condition<Int>>()

    private val name = "test0"
    private val state = 42

    @Test
    fun `Test name`() {
        val and = And(name, listOf(c0, c1, c2))

        assertThat(and.getName()).isSameAs(name)
    }

    @Test
    fun `Test default name`() {
        every { c0.getName() } returns "0"
        every { c1.getName() } returns "1"
        every { c2.getName() } returns "2"

        val and = And(listOf(c0, c1, c2))

        assertThat(and.getName()).isEqualTo("And(0, 1, 2)")
    }

    @Test
    fun `All conditions are true`() {
        testCheck(r0 = true, r1 = true, r2 = true, result = true)
    }

    @Test
    fun `First conditions is false`() {
        testCheck(r0 = false, r1 = true, r2 = true, result = false)
    }

    @Test
    fun `Second conditions is false`() {
        testCheck(r0 = true, r1 = false, r2 = true, result = false)
    }

    @Test
    fun `Third conditions is false`() {
        testCheck(r0 = true, r1 = true, r2 = false, result = false)
    }

    private fun testCheck(r0: Boolean, r1: Boolean, r2: Boolean, result: Boolean) {
        every { c0.check(state) } returns r0
        every { c1.check(state) } returns r1
        every { c2.check(state) } returns r2

        val and = And(name, listOf(c0, c1, c2))

        assertThat(and.check(state)).isEqualTo(result)

        verify { c0.check(state) }
        if (r0) verify { c1.check(state) }
        if (r0 && r1) verify { c2.check(state) }
        confirmVerified(c0)
        confirmVerified(c1)
        confirmVerified(c2)
    }
}