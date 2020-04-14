package game.rpg.check

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import util.redux.random.RandomNumberGenerator

private const val SIDES = 6

@ExtendWith(MockKExtension::class)
class CheckerTest {

    @MockK
    lateinit var rng: RandomNumberGenerator

    val checker = Checker(SIDES)

    @Nested
    inner class GetCriticalSuccess {

        @Test
        fun `Get a critical success`() {
            every { rng.rollDice(SIDES) } returnsMany (listOf(SIDES, SIDES))

            assertThat(checker.check(rng, 0, 0)).isEqualTo(Success(5))

            verify(exactly = 2) { rng.rollDice(SIDES) }
        }

        @Test
        fun `Get a critical success, where a success would be impossible`() {
            every { rng.rollDice(SIDES) } returnsMany (listOf(SIDES, SIDES))

            assertThat(checker.check(rng, 0, 100)).isEqualTo(Failure(95))

            verify(exactly = 2) { rng.rollDice(SIDES) }
        }
    }

    @Nested
    inner class GetCriticalFailure {

        @Test
        fun `Get a critical failure`() {
            every { rng.rollDice(SIDES) } returnsMany (listOf(1, 1))

            assertThat(checker.check(rng, 0, 0)).isEqualTo(Failure(5))

            verify(exactly = 2) { rng.rollDice(SIDES) }
        }

        @Test
        fun `Get a critical success, where a failure would be impossible`() {
            every { rng.rollDice(SIDES) } returnsMany (listOf(1, 1))

            assertThat(checker.check(rng, 100, 0)).isEqualTo(Success(95))

            verify(exactly = 2) { rng.rollDice(SIDES) }
        }
    }

    @Test
    fun `Test draw`() {
        every { rng.rollDice(SIDES) } returnsMany (listOf(3, 3))

        assertThat(checker.check(rng, 10, 10)).isSameAs(Draw)

        verify(exactly = 2) { rng.rollDice(SIDES) }
    }

    @Test
    fun `Test success and its rank`() {
        every { rng.rollDice(SIDES) } returnsMany (listOf(5, 3))

        assertThat(checker.check(rng, 4, 3)).isEqualTo(Success(3))

        verify(exactly = 2) { rng.rollDice(SIDES) }
    }

    @Test
    fun `Test failure and its rank`() {
        every { rng.rollDice(SIDES) } returnsMany (listOf(1, 6))

        assertThat(checker.check(rng, 4, 3)).isEqualTo(Failure(4))

        verify(exactly = 2) { rng.rollDice(SIDES) }
    }

}