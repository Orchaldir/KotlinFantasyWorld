package ai.behavior.bt.composite

import ai.behavior.bt.*
import assertk.assertThat
import assertk.assertions.isInstanceOf
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class SequenceTest {

    private val b0 = mockk<Behavior<Int, String>>()
    private val b1 = mockk<Behavior<Int, String>>()
    private val b2 = mockk<Behavior<Int, String>>()

    private val success = Success<Int>()

    private val sequence = Sequence(listOf(b0, b1, b2))
    private val state = "STATE"

    @Test
    fun `First behavior executes an action`() {
        test(0, PerformAction(1))
    }

    @Test
    fun `Second behavior executes an action`() {
        test(1, PerformAction(2))
    }

    @Test
    fun `Third behavior executes an action`() {
        test(2, PerformAction(3))
    }

    @Test
    fun `All behaviors return success`() {
        test(3, success)
    }

    @Test
    fun `Second behavior fails`() {
        test(1, Failure())
    }

    private fun test(index: Int, status: Status<Int>) {
        mockExecute(b0, status, index, 0)
        mockExecute(b1, status, index, 1)
        mockExecute(b2, status, index, 2)

        assertThat(sequence.execute(state)).isInstanceOf(status::class)

        verify { b0.execute(state) }
        if (index > 0) verify { b1.execute(state) }
        if (index > 1) verify { b2.execute(state) }
        confirmVerified(b0)
        confirmVerified(b1)
        confirmVerified(b2)
    }

    private fun mockExecute(
        behavior: Behavior<Int, String>,
        status: Status<Int>,
        index: Int,
        desired: Int
    ) {
        every { behavior.execute(state) } returns if (index == desired) status else success
    }
}