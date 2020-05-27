package game.behavior.bt

import ai.behavior.bt.*
import game.action.Action
import game.component.Body
import game.component.getPositionsAround
import game.map.GameMap
import mu.KotlinLogging
import util.ecs.EcsState

private val logger = KotlinLogging.logger {}

class FindGoalsBehavior : Behavior<Action, EcsState> {

    override fun execute(state: EcsState, blackboard: Blackboard): Status<Action> {
        val self = blackboard.get<Int>("self")
        val target = blackboard.get<Int>("target")

        logger.info("Find goals around entity $target for entity $self")

        val selfBody = state.getStorage<Body>()[self]!!

        val gameMap = state.getData<GameMap>()

        val targetBody = state.getStorage<Body>()[target]!!
        val goals = getPositionsAround(gameMap.size, selfBody, targetBody)

        return if (goals.isEmpty()) {
            Failure()
        } else {
            blackboard.put("goals", goals)
            Success()
        }
    }

}