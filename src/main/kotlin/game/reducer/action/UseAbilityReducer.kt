package game.reducer.action

import game.CannotTargetSelf
import game.OutOfRangeException
import game.action.Action
import game.action.OnDamage
import game.action.UseAbility
import game.component.*
import game.map.GameMap
import game.rpg.character.ability.DamageEffect
import game.rpg.character.ability.MeleeAttack
import game.rpg.check.Checker
import game.rpg.check.Failure
import game.rpg.time.TimeSystem
import game.rpg.time.TurnData
import util.ecs.EcsState
import util.log.Message
import util.log.MessageLog
import util.log.inform
import util.redux.Reducer
import util.redux.random.RandomNumberState

val USE_ABILITY_REDUCER: Reducer<Action, EcsState> = a@{ state, action ->
    if (action !is UseAbility) throw IllegalArgumentException("Action must be UseAbility!")

    state.getData<TimeSystem>().validateCurrent(action.entity)

    val turnData = state.getData<TurnData>()
    val newTurnData = turnData.reduceActionPoints(action.entity)

    val map = state.getData<GameMap>()
    val target =
        map.entities[action.position] ?: throw  IllegalStateException("No target at position ${action.position}")

    if (action.entity == target) throw CannotTargetSelf(target)

    val ability = getAbility(state = state, entity = action.entity, index = action.ability)

    val rng = state.getData<RandomNumberState>().createGenerator()
    val checker = state.getData<Checker>()

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

            when (checker.check(rng, attackRank, defenseRank)) {
                is Failure -> messages += inform(state, "%s missed %s", action.entity, target)
                else -> {
                    messages += inform(state, "%s hits %s", action.entity, target)

                    when (val effect = ability.effect) {
                        is DamageEffect -> {
                            events += OnDamage(target, effect.damage)
                        }
                    }
                }
            }
        }
    }

    val messageLog = state.getData<MessageLog>().add(messages)

    Pair(state.copy(updatedData = listOf(messageLog, rng.createState(), newTurnData)), events)
}
