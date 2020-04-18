package game.reducer

import game.action.Move
import game.component.BigBody
import game.component.Body
import game.component.SimpleBody
import game.component.SnakeBody
import game.map.*
import game.rpg.time.TurnData
import javafx.scene.paint.Color.WHITE
import javafx.scene.paint.Color.YELLOW
import util.ecs.EcsState
import util.ecs.storage.ComponentStorage
import util.log.Message
import util.log.addMessage
import util.math.Direction
import util.redux.Reducer

val MOVE_REDUCER: Reducer<Move, EcsState> = a@{ state, action ->
    val turnData = state.getData<TurnData>()

    if (turnData.movementPoints <= 0) {
        return@a addMessage(state, Message("No movement points", YELLOW))
    }

    val map = state.getData<GameMap>()
    val bodyStorage = state.getStorage<Body>()
    val body = bodyStorage.getOrThrow(action.entity)

    val walkability = getNewPosition(map, action.entity, body, action.direction)

    when (walkability) {
        is Walkable -> move(state, map, action.entity, bodyStorage, body, walkability, turnData)
        BlockedByObstacle -> addMessage(state, Message("Blocked by obstacle", WHITE))
        is BlockedByEntity -> addMessage(state, Message("Blocked by entity ${walkability.entity}", WHITE))
        OutsideMap -> addMessage(state, Message("Blocked by map border", WHITE))
    }
}


fun getNewPosition(map: GameMap, entity: Int, body: Body, direction: Direction) = when (body) {
    is SimpleBody -> map.size.getNeighbor(body.position, direction) then { position ->
        map.checkWalkability(
            position,
            entity = entity
        )
    }
    is BigBody -> map.size.getNeighbor(body.position, direction) then { position ->
        map.checkWalkability(
            position,
            size = body.size,
            entity = entity
        )
    }
    is SnakeBody -> map.size.getNeighbor(body.positions.first(), direction) then { position ->
        map.checkWalkability(
            position,
            entity = entity
        )
    }
}

private fun move(
    state: EcsState,
    map: GameMap,
    entity: Int,
    bodyStorage: ComponentStorage<Body>,
    body: Body,
    walkable: Walkable,
    turnData: TurnData
): EcsState {
    val newMap = updateMap(map, entity, body, walkable.position)

    val newBody = updateBody(body, walkable.position)
    val newBodyStorage = bodyStorage.updateAndRemove(mapOf(entity to newBody))

    val newTurnData = turnData.reduceMovementPoints()

    return state.copy(listOf(newBodyStorage), listOf(newMap, newTurnData))
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