package game.reducer

import game.MoveAction
import game.component.BigBody
import game.component.Body
import game.component.SimpleBody
import game.component.SnakeBody
import game.map.GameMap
import util.ecs.EcsState
import util.math.Direction
import util.redux.Reducer

val MOVE_REDUCER: Reducer<MoveAction, EcsState> = { state, action ->
    val map = state.getData<GameMap>() ?: throw IllegalStateException("No map!")
    val bodyStorage = state.get<Body>() ?: throw IllegalStateException("No body storage!")
    val body = bodyStorage[action.entity] ?: throw IllegalStateException("Entity ${action.entity} has no body!")

    val newPosition = getNewPosition(map, action.entity, body, action.direction)

    if (newPosition != null) {
        val newMap = updateMap(map, action.entity, body, newPosition)
        val newBody = updateBody(body, newPosition)
        val newBodyStorage = bodyStorage.updateAndRemove(mapOf(action.entity to newBody))
        state.copy(mapOf(Body::class to newBodyStorage), mapOf(GameMap::class to newMap))
    } else {
        state
    }
}

fun getNewPosition(map: GameMap, entity: Int, body: Body, direction: Direction) = when (body) {
    is SimpleBody -> map.size.getNeighbor(body.position, direction)?.takeIf {
        map.isWalkable(
            index = it,
            entity = entity
        )
    }
    is BigBody -> map.size.getNeighbor(body.position, direction)?.takeIf {
        map.isWalkable(
            index = it,
            size = body.size,
            entity = entity
        )
    }
    is SnakeBody -> map.size.getNeighbor(body.positions.first(), direction)?.takeIf {
        map.isWalkable(
            index = it,
            entity = entity
        )
    }
}

fun updateMap(map: GameMap, entity: Int, body: Body, position: Int) = when (body) {
    is SimpleBody -> map.builder()
        .removeEntity(index = body.position, entity = entity)
        .setEntity(index = position, entity = entity)
        .build()
    is BigBody -> map.builder()
        .removeEntity(index = body.position, entity = entity, size = body.size)
        .setEntity(index = position, entity = entity, size = body.size)
        .build()
    is SnakeBody -> with(map.builder()) {
        val last = body.positions.last()
        if (body.positions.count { i -> i == last } == 1) {
            removeEntity(index = last, entity = entity)
        }
        setEntity(index = position, entity = entity)
        build()
    }
}

fun updateBody(body: Body, position: Int) = when (body) {
    is SimpleBody -> SimpleBody(position)
    is BigBody -> BigBody(position, body.size)
    is SnakeBody -> {
        val positions = body.positions.toMutableList()
        positions.removeAt(positions.lastIndex)
        SnakeBody(listOf(position) + positions)
    }
}