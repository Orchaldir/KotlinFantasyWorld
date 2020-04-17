package game.reducer

import game.InitAction
import game.component.*
import game.map.GameMap
import game.map.GameMapBuilder
import game.rpg.time.TimeSystem
import javafx.scene.paint.Color
import util.ecs.EcsState
import util.log.Message
import util.log.MessageLog
import util.redux.Reducer
import kotlin.reflect.KClass

val INIT_REDUCER: Reducer<InitAction, EcsState> = { state, _ ->
    val updatedData = mutableMapOf<KClass<*>, Any>()

    initBodies(state, updatedData)
    initMessageLog(state, updatedData)
    initTime(state, updatedData)

    state.copy(updatedDataMap = updatedData)
}

fun initBodies(state: EcsState, updatedData: MutableMap<KClass<*>, Any>) {
    val map = state.getData<GameMap>()
    val bodyStorage = state.getStorage<Body>()

    val mapBuilder = map.builder()

    for (id in bodyStorage.getIds()) {
        val body = bodyStorage.getOrThrow(id)
        addToMap(mapBuilder, id, body)
    }

    updatedData[GameMap::class] = mapBuilder.build()
}

fun initMessageLog(state: EcsState, updatedData: MutableMap<KClass<*>, Any>) {
    val messageLog = state.getData<MessageLog>()
    updatedData[MessageLog::class] = messageLog.add(Message("Init game", Color.WHITE))
}

fun initTime(state: EcsState, updatedData: MutableMap<KClass<*>, Any>) {
    val system = state.getOptionalData<TimeSystem>()

    if (system != null) {
        val controllerStorage = state.getStorage<Controller>()
        updatedData[TimeSystem::class] = system.add(controllerStorage.getIds().sorted())
    }
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