package util.redux.random

class RandomNumberGenerator(private val state: RandomNumberState) {
    private var index = state.index

    fun getInt() = state.getNumber(index++)

    fun rollDice(sides: Int) = getInt() % sides + 1

    fun rollPositiveAndNegativeDice(sides: Int): Int {
        val positiveDice = rollDice(sides)
        val negativeDice = rollDice(sides)
        return positiveDice - negativeDice
    }

    fun createState(): RandomNumberState {
        return state.copy(index)
    }

}