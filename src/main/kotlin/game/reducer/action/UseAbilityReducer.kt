package game.reducer.action

import game.InvalidAbilityUsageException
import game.action.Action
import game.action.OnDamage
import game.action.UseAbility
import game.component.getAbility
import game.component.getDefense
import game.component.getRank
import game.rpg.character.ability.*
import game.rpg.check.Checker
import game.rpg.check.Failure
import game.rpg.time.TimeSystem
import game.rpg.time.TurnData
import util.ecs.EcsState
import util.log.Message
import util.log.MessageLog
import util.log.inform
import util.redux.Reducer
import util.redux.random.RandomNumberGenerator
import util.redux.random.RandomNumberState

val USE_ABILITY_REDUCER: Reducer<Action, EcsState> = a@{ state, action ->
    if (action !is UseAbility) throw IllegalArgumentException("Action must be UseAbility!")

    state.getData<TimeSystem>().validateCurrent(action.entity)

    val ability = getAbility(state = state, entity = action.entity, index = action.ability)
    val result = checkAbility(state, ability, action.entity, action.position)

    if (result !is ValidUsage) throw InvalidAbilityUsageException(result)

    val turnData = state.getData<TurnData>()
    val newTurnData = turnData.reduceActionPoints(action.entity)

    val rng = state.getData<RandomNumberState>().createGenerator()

    val events = mutableListOf<Action>()
    val messages = mutableListOf<Message>()

    when (ability) {
        is MeleeAttack -> {
            val defense = getDefense(state, result.target)

            val attackRank = getRank(state, action.entity, ability.skill)
            val defenseRank = getRank(state, result.target, defense.skill)

            handleEffect(
                state,
                messages,
                events,
                rng,
                action.entity,
                result.target,
                attackRank,
                defenseRank,
                ability.effect
            )
        }
        is RangedAttack -> {
            val attackRank = getRank(state, action.entity, ability.skill)
            val difficulty = when {
                result.distance < ability.shortRange -> 2
                result.distance < ability.mediumRange -> 4
                else -> 6
            }

            handleEffect(
                state,
                messages,
                events,
                rng,
                action.entity,
                result.target,
                attackRank,
                difficulty,
                ability.effect
            )
        }
    }

    val messageLog = state.getData<MessageLog>().add(messages)

    Pair(state.copy(updatedData = listOf(messageLog, rng.createState(), newTurnData)), events)
}

private fun handleEffect(
    state: EcsState,
    messages: MutableList<Message>,
    events: MutableList<Action>,
    rng: RandomNumberGenerator,
    entity: Int,
    target: Int,
    attackRank: Int,
    defenseRank: Int,
    effect: Effect
) {
    val checker = state.getData<Checker>()

    when (checker.check(rng, attackRank, defenseRank)) {
        is Failure -> messages += inform(state, "%s missed %s", entity, target)
        else -> {
            messages += inform(state, "%s hits %s", entity, target)

            when (effect) {
                is DamageEffect -> {
                    events += OnDamage(target, effect.damage)
                }
            }
        }
    }
}
