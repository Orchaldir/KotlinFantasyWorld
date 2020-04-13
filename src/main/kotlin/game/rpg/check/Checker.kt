package game.rpg.check

import util.redux.random.RandomNumberGenerator

class Checker(
    private val diceSide: Int
) {

    fun check(generator: RandomNumberGenerator, rank: Int, difficulty: Int): CheckResult {
        val positiveDice = generator.rollDice(diceSide)
        val negativeDice = generator.rollDice(diceSide)

        if (positiveDice == diceSide && negativeDice == diceSide) {
            return CriticalSuccess
        } else if (positiveDice == 1 && negativeDice == 1) {
            return CriticalFailure
        }

        val diff = rank - difficulty + positiveDice - negativeDice

        return when {
            diff > 0 -> Success(diff)
            diff < 0 -> Failure(-diff)
            else -> Draw
        }
    }

}