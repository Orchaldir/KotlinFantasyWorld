package ai.behavior.bt.composite

import ai.behavior.bt.Behavior
import ai.behavior.bt.Failure
import ai.behavior.bt.PerformAction
import ai.behavior.bt.Success
import assertk.assertThat
import assertk.assertions.isEqualTo
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
        every { b0.execute(state) } returns PerformAction(1)

        assertThat(sequence.execute(state)).isEqualTo(PerformAction(1))

        verify { b0.execute(state) }
        confirmVerified(b0)
        confirmVerified(b1)
        confirmVerified(b2)
    }

    @Test
    fun `Second behavior executes an action`() {
        every { b0.execute(state) } returns success
        every { b1.execute(state) } returns PerformAction(2)

        assertThat(sequence.execute(state)).isEqualTo(PerformAction(2))

        verify { b0.execute(state) }
        verify { b1.execute(state) }
        confirmVerified(b0)
        confirmVerified(b1)
        confirmVerified(b2)
    }

    @Test
    fun `Third behavior executes an action`() {
        every { b0.execute(state) } returns success
        every { b1.execute(state) } returns success
        every { b2.execute(state) } returns PerformAction(3)

        assertThat(sequence.execute(state)).isEqualTo(PerformAction(3))

        verify { b0.execute(state) }
        verify { b1.execute(state) }
        verify { b2.execute(state) }
        confirmVerified(b0)
        confirmVerified(b1)
        confirmVerified(b2)
    }

    @Test
    fun `All behaviors return success`() {
        every { b0.execute(state) } returns success
        every { b1.execute(state) } returns success
        every { b2.execute(state) } returns success

        assertThat(sequence.execute(state)).isInstanceOf(Success::class)

        verify { b0.execute(state) }
        verify { b1.execute(state) }
        verify { b2.execute(state) }
        confirmVerified(b0)
        confirmVerified(b1)
        confirmVerified(b2)
    }

    @Test
    fun `Second behavior fails`() {
        every { b0.execute(state) } returns success
        every { b1.execute(state) } returns Failure()

        assertThat(sequence.execute(state)).isInstanceOf(Failure::class)

        verify { b0.execute(state) }
        verify { b1.execute(state) }
        confirmVerified(b0)
        confirmVerified(b1)
        confirmVerified(b2)
    }
}