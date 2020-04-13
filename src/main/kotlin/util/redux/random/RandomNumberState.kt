package util.redux.random

import kotlin.random.Random

data class RandomNumberState(
    val numbers: List<Int>,
    val index: Int = 0
) {

    fun getNumber(index: Int) = numbers[index % numbers.size]

    fun copy(newIndex: Int) = if (newIndex == index) {
        this
    } else RandomNumberState(numbers, newIndex)

    fun createGenerator() = RandomNumberGenerator(this)
}

fun init(random: Random, size: Int): RandomNumberState {
    val numbers = mutableListOf<Int>();

    repeat(size) { numbers.add(random.nextInt(Int.MAX_VALUE)) }

    return RandomNumberState(numbers, 0)
}