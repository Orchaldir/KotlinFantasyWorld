package game.reducer.action

import game.action.FollowPath
import game.component.BigBody
import game.component.Body
import game.map.GameMap
import game.map.Walkable
import game.rpg.time.TurnData
import util.ecs.EcsState
import util.log.addMessage
import util.log.warn
import util.redux.Reducer
import util.redux.noFollowUps

val FOLLOW_PATH_REDUCER: Reducer<FollowPath, EcsState> = a@{ state, action ->
    var turnData = state.getData<TurnData>()

    if (turnData.movementPoints <= 0) {
        return@a noFollowUps(addMessage(state, warn("No movement points")))
    }

    val entity = action.entity
    var map = state.getData<GameMap>()
    val bodyStorage = state.getStorage<Body>()
    var body = bodyStorage.getOrThrow(entity)
    var pathIndex = 0

    while (turnData.movementPoints > 0 && pathIndex < action.path.indices.size) {
        val newPosition = action.path.indices[pathIndex]
        val walkability = checkWalkability(map, entity, body, newPosition)

        when (walkability) {
            is Walkable -> {
                turnData = turnData.reduceMovementPoints(action.entity)
                map = updateMap(map, entity, body, newPosition)
                body = updateBody(body, newPosition)
            }
            else -> return@a noFollowUps(handleError(state, walkability))
        }

        pathIndex++
    }

    val newBodyStorage = bodyStorage.updateAndRemove(mapOf(entity to body))

    noFollowUps(state.copy(listOf(newBodyStorage), listOf(map, turnData)))
}

fun checkWalkability(map: GameMap, entity: Int, body: Body, position: Int) = when (body) {
    is BigBody -> map.checkWalkability(position = position, size = body.size, entity = entity)
    else -> map.checkWalkability(position = position, entity = entity)
}