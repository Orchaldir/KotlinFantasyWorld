package game.reducer.action

import game.CannotTargetSelfException
import game.OutOfRangeException
import game.action.Action
import game.action.OnDamage
import game.action.UseAbility
import game.component.*
import game.map.GameMap
import game.rpg.character.ability.DamageEffect
import game.rpg.character.ability.Effect
import game.rpg.character.ability.MeleeAttack
import game.rpg.character.ability.RangedAttack
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

    val turnData = state.getData<TurnData>()
    val newTurnData = turnData.reduceActionPoints(action.entity)

    val map = state.getData<GameMap>()
    val target =
        map.entities[action.position] ?: throw  IllegalStateException("No target at position ${action.position}")

    if (action.entity == target) throw CannotTargetSelfException(target)

    val ability = getAbility(state = state, entity = action.entity, index = action.ability)

    val rng = state.getData<RandomNumberState>().createGenerator()

    val body = state.getStorage<Body>().getOrThrow(action.entity)

    val events = mutableListOf<Action>()
    val messages = mutableListOf<Message>()

    when (ability) {
        is MeleeAttack -> {
            val distance = calculateDistanceToPosition(map.size, body, action.position)

            if (distance > ability.reach) throw OutOfRangeException(distance)

            val defense = getDefense(state, target)

            val attackRank = getRank(state, action.entity, ability.skill)
            val defenseRank = getRank(state, target, defense.skill)

            handleEffect(
                state,
                messages,
                events,
                rng,
                action.entity,
                target,
                attackRank,
                defenseRank,
                ability.effect
            )
        }
        is RangedAttack -> {
            val distance = calculateDistanceToPosition(map.size, body, action.position)

            if (distance > ability.longRange) throw OutOfRangeException(distance)

            val attackRank = getRank(state, action.entity, ability.skill)
            val difficulty = when {
                distance < ability.shortRange -> 2
                distance < ability.mediumRange -> 4
                else -> 6
            }

            handleEffect(
                state,
                messages,
                events,
                rng,
                action.entity,
                target,
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
