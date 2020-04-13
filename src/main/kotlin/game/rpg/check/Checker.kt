package game.rpg.check

import util.redux.random.RandomNumberGenerator

class Checker(
    private val diceSide: Int
) {

    fun check(rng: RandomNumberGenerator, rank: Int, difficulty: Int): CheckResult {
        val positiveDice = rng.rollDice(diceSide)
        val negativeDice = rng.rollDice(diceSide)

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