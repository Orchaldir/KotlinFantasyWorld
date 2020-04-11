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
    state
}

fun getNewPosition(map: GameMap, entity: Int, body: Body, direction: Direction): Int? {
    return when (body) {
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
}