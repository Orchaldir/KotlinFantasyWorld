package game.reducer

import game.InitAction
import game.component.BigBody
import game.component.Body
import game.component.SimpleBody
import game.component.SnakeBody
import game.map.GameMap
import game.map.GameMapBuilder
import javafx.scene.paint.Color
import util.ecs.EcsState
import util.log.Message
import util.log.MessageLog
import util.redux.Reducer

val INIT_REDUCER: Reducer<InitAction, EcsState> = { state, _ ->
    val map = state.getData<GameMap>()
    val bodyStorage = state.getStorage<Body>()

    val mapBuilder = map.builder()

    for (id in bodyStorage.getIds()) {
        val body = bodyStorage.getOrThrow(id)
        addToMap(mapBuilder, id, body)
    }

    val newMap = mapBuilder.build()

    val messageLog = state.getData<MessageLog>()
    val newMessageLog = messageLog.add(Message("Init game", Color.WHITE))

    state.copy(updatedDataMap = mapOf(GameMap::class to newMap, MessageLog::class to newMessageLog))
}

fun addToMap(builder: GameMapBuilder, entity: Int, body: Body) = when (body) {
    is SimpleBody -> builder
        .setEntity(index = body.position, entity = entity)
    is BigBody -> builder
        .setEntity(index = body.position, entity = entity, size = body.size)
    is SnakeBody -> {
        body.positions.forEach { p -> builder.setEntity(p, entity) }
        builder
    }
}