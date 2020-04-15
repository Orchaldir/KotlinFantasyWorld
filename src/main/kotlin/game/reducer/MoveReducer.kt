package game.reducer

import game.MoveAction
import game.component.BigBody
import game.component.Body
import game.component.SimpleBody
import game.component.SnakeBody
import game.map.GameMap
import game.map.Walkability.WALKABLE
import util.ecs.EcsState
import util.math.Direction
import util.redux.Reducer

val MOVE_REDUCER: Reducer<MoveAction, EcsState> = { state, action ->
    val map = state.getData<GameMap>()
    val bodyStorage = state.getStorage<Body>()
    val body = bodyStorage.getOrThrow(action.entity)

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
        map.checkWalkability(
            index = it,
            entity = entity
        ) == WALKABLE
    }
    is BigBody -> map.size.getNeighbor(body.position, direction)?.takeIf {
        map.checkWalkability(
            index = it,
            size = body.size,
            entity = entity
        ) == WALKABLE
    }
    is SnakeBody -> map.size.getNeighbor(body.positions.first(), direction)?.takeIf {
        map.checkWalkability(
            index = it,
            entity = entity
        ) == WALKABLE
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