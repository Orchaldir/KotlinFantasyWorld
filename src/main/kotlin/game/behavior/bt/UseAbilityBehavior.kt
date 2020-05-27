package game.behavior.bt

import ai.behavior.bt.*
import game.action.Action
import game.action.FinishTurn
import game.action.UseAbility
import game.component.Body
import game.component.getAbility
import game.component.getPositionsUnderBody
import game.map.GameMap
import game.rpg.character.ability.NoActionPoints
import game.rpg.character.ability.OutOfRange
import game.rpg.character.ability.ValidUsage
import game.rpg.character.ability.checkAbility
import mu.KotlinLogging
import util.ecs.EcsState

private val logger = KotlinLogging.logger {}

class UseAbilityBehavior : Behavior<Action, EcsState> {

    override fun execute(state: EcsState, blackboard: Blackboard): Status<Action> {
        val self = blackboard.get<Int>("self")
        val abilityIndex = blackboard.get<Int>("ability")
        val target = blackboard.get<Int>("target")

        logger.info("Entity $self use ability $abilityIndex on entity $target")

        val ability = getAbility(state, self, abilityIndex)

        val targetBody = state.getStorage<Body>()[target]!!
        val gameMap = state.getData<GameMap>()

        getPositionsUnderBody(gameMap.size, targetBody).forEach {
            when (checkAbility(state, ability, self, it)) {
                is ValidUsage -> return PerformAction(UseAbility(self, abilityIndex, it))
                is OutOfRange -> {
                }
                is NoActionPoints -> return PerformAction(FinishTurn(self))
                else -> return Failure()
            }
        }

        return Failure()
    }

}