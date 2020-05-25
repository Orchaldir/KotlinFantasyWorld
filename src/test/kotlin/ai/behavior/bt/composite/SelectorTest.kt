package ai.behavior.bt.composite

import ai.behavior.bt.*
import assertk.assertThat
import assertk.assertions.isInstanceOf
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class SelectorTest {

    private val b0 = mockk<Behavior<Int, String>>()
    private val b1 = mockk<Behavior<Int, String>>()
    private val b2 = mockk<Behavior<Int, String>>()

    private val blackboard = mockk<Blackboard>()

    private val failure = Failure<Int>()
    private val success = Success<Int>()

    private val selector = Selector(listOf(b0, b1, b2))
    private val state = "STATE"

    @Test
    fun `First behavior succeeds`() {
        test(0, success)
    }

    @Test
    fun `First behavior executed action`() {
        test(0, PerformAction(1))
    }

    @Test
    fun `Second behavior succeeds`() {
        test(1, success)
    }

    @Test
    fun `Second behavior executed action`() {
        test(1, PerformAction(2))
    }

    @Test
    fun `Third behavior succeeds`() {
        test(2, success)
    }

    @Test
    fun `Third behavior executed action`() {
        test(2, PerformAction(3))
    }

    @Test
    fun `All behaviors fail`() {
        test(3, failure)
    }

    private fun test(index: Int, status: Status<Int>) {
        mockExecute(b0, status, index, 0)
        mockExecute(b1, status, index, 1)
        mockExecute(b2, status, index, 2)

        assertThat(selector.execute(state, blackboard)).isInstanceOf(status::class)

        verify { b0.execute(state, blackboard) }
        if (index > 0) verify { b1.execute(state, blackboard) }
        if (index > 1) verify { b2.execute(state, blackboard) }
        confirmVerified(b0)
        confirmVerified(b1)
        confirmVerified(b2)

        confirmVerified(blackboard)
    }

    private fun mockExecute(
        behavior: Behavior<Int, String>,
        status: Status<Int>,
        index: Int,
        desired: Int
    ) {
        every { behavior.execute(state, blackboard) } returns if (index == desired) status else failure
    }
}