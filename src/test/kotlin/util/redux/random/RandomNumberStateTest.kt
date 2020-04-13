package util.redux.random

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotSameAs
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.random.Random

private val NUMBERS = listOf(10, 11, 12)

class RandomNumberStateTest {

    @Test
    fun `Test constructor`() {
        val state = RandomNumberState(NUMBERS, 2)

        assertThat(state.numbers).isSameAs(NUMBERS)
        assertThat(state.index).isSameAs(2)
    }

    @Nested
    inner class GetNumber {

        @Test
        fun `Get all numbers`() {
            val state = RandomNumberState(NUMBERS)

            assertThat(state.getNumber(0)).isSameAs(10)
            assertThat(state.getNumber(1)).isSameAs(11)
            assertThat(state.getNumber(2)).isSameAs(12)
        }

        @Test
        fun `Index rolls over`() {
            val state = RandomNumberState(NUMBERS)

            assertThat(state.getNumber(3)).isSameAs(10)
            assertThat(state.getNumber(4)).isSameAs(11)
            assertThat(state.getNumber(5)).isSameAs(12)
        }
    }

    @Nested
    inner class Copy {

        @Test
        fun `Copy with new index`() {
            val state = RandomNumberState(NUMBERS)
            val newState = state.copy(2)

            assertThat(newState).isNotSameAs(state)
            assertThat(newState.numbers).isSameAs(NUMBERS)
            assertThat(newState.index).isSameAs(2)
        }

        @Test
        fun `Copy with same index`() {
            val state = RandomNumberState(NUMBERS, 1)
            val newState = state.copy(1)

            assertThat(newState).isSameAs(state)
            assertThat(newState.numbers).isSameAs(NUMBERS)
            assertThat(newState.index).isSameAs(1)
        }
    }

    @Test
    fun `Test init with random numbers`() {
        val n = 5
        val state = init(Random(0), n)

        assertThat(state.index).isSameAs(0)

        (0..n).forEach {
            assertThat(state.getNumber(it)).isEqualTo(state.getNumber(it + n))
        }
    }
}