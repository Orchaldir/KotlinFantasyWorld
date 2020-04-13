package util.redux.random

import assertk.assertThat
import assertk.assertions.isNotSameAs
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val NUMBERS = listOf(20, 21, 22)

class RandomNumberGeneratorTest {

    @Nested
    inner class GetInt {

        @Test
        fun `Get all numbers`() {
            val state = RandomNumberState(NUMBERS)
            val generator = RandomNumberGenerator(state)

            assertThat(generator.getInt()).isSameAs(20)
            assertThat(generator.getInt()).isSameAs(21)
            assertThat(generator.getInt()).isSameAs(22)
        }

        @Test
        fun `Index rolls over`() {
            val state = RandomNumberState(NUMBERS, 2)
            val generator = RandomNumberGenerator(state)

            assertThat(generator.getInt()).isSameAs(22)
            assertThat(generator.getInt()).isSameAs(20)
        }
    }

    @Nested
    inner class GetDice {

        @Test
        fun `Integer is smaller than die side`() {
            val state = RandomNumberState(listOf(3))
            val generator = RandomNumberGenerator(state)

            assertThat(generator.rollDice(6)).isSameAs(4)
        }

        @Test
        fun `Integer is lager than die side`() {
            val state = RandomNumberState(listOf(8))
            val generator = RandomNumberGenerator(state)

            assertThat(generator.rollDice(6)).isSameAs(3)
        }

    }

    @Test
    fun `Roll a positive & negative dice`() {
        val state = RandomNumberState(listOf(4, 1, 3, 5))
        val generator = RandomNumberGenerator(state)

        assertThat(generator.rollPositiveAndNegativeDice(6)).isSameAs(3)
        assertThat(generator.rollPositiveAndNegativeDice(6)).isSameAs(-2)
    }

    @Test
    fun `Create a new state`() {
        val numbers = listOf(0, 1)
        val state = RandomNumberState(numbers)
        val generator = state.createGenerator()

        generator.rollPositiveAndNegativeDice(6)

        val newState = generator.createState()

        assertThat(newState).isNotSameAs(state)
        assertThat(newState.numbers).isSameAs(numbers)
        assertThat(newState.index).isSameAs(2)
    }
}