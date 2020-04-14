package game.rpg.check

import util.redux.random.RandomNumberGenerator

class Checker(
    private val diceSide: Int
) {
    private val criticalResultBonus = diceSide - 1

    fun check(rng: RandomNumberGenerator, rank: Int, difficulty: Int): CheckResult {
        val positiveDice = rng.rollDice(diceSide)
        val negativeDice = rng.rollDice(diceSide)
        val criticalModifier = calculateCriticalModifier(positiveDice, negativeDice)

        val diff = rank - difficulty + positiveDice - negativeDice + criticalModifier

        return when {
            diff > 0 -> Success(diff)
            diff < 0 -> Failure(-diff)
            else -> Draw
        }
    }

    private fun calculateCriticalModifier(positiveDice: Int, negativeDice: Int) =
        if (positiveDice == diceSide && negativeDice == diceSide) {
            criticalResultBonus
        } else if (positiveDice == 1 && negativeDice == 1) {
            -criticalResultBonus
        } else {
            0
        }

}