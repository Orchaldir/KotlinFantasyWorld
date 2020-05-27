package game.behavior.bt

import ai.behavior.bt.*
import ai.pathfinding.AStar
import ai.pathfinding.GoalAlreadyReached
import ai.pathfinding.Path
import game.action.Action
import game.action.FinishTurn
import game.action.FollowPath
import game.component.Body
import game.component.getPosition
import game.component.getSize
import game.map.GameMap
import game.rpg.time.TurnData
import mu.KotlinLogging
import util.ecs.EcsState
import util.math.rectangle.DistanceCalculator

private val logger = KotlinLogging.logger {}

class MoveToGoalBehavior : Behavior<Action, EcsState> {

    override fun execute(state: EcsState, blackboard: Blackboard): Status<Action> {
        val self = blackboard.get<Int>("self")
        val goals = blackboard.get<Set<Int>>("goals")

        logger.info("Move entity $self towards $goals")

        val turnData = state.getData<TurnData>()

        val selfBody = state.getStorage<Body>()[self]!!
        val selfPosition = getPosition(selfBody)
        val selfSize = getSize(selfBody)

        val distanceCalculator = state.getData<DistanceCalculator>()
        val gameMap = state.getData<GameMap>()
        val occupancyMap = gameMap.createOccupancyMap(distanceCalculator, selfSize, self)

        val pathfinding = AStar<Boolean>()
        val result = pathfinding.find(occupancyMap, selfPosition, goals, selfSize)

        logger.info("Pathfinding result is $result")

        return when (result) {
            is Path -> PerformAction(if (turnData.movementPoints > 0) FollowPath(self, result) else FinishTurn(self))
            is GoalAlreadyReached -> Success()
            else -> Failure()
        }
    }

}