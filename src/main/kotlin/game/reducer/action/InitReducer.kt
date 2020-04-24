package game.reducer.action

import game.action.Init
import game.component.*
import game.map.GameMap
import game.map.GameMapBuilder
import game.rpg.character.skill.Skill
import game.rpg.character.skill.SkillUsage
import game.rpg.time.TimeSystem
import game.rpg.time.createTurnData
import util.ecs.EcsState
import util.log.MessageLog
import util.log.inform
import util.redux.Reducer
import util.redux.noFollowUps

val INIT_REDUCER: Reducer<Init, EcsState> = { state, _ ->
    val skillUsage = state.getData<SkillUsage>()
    val updatedData = mutableListOf<Any>()

    initBodies(state, updatedData)
    initMessageLog(state, updatedData)
    initTime(state, updatedData, skillUsage.speed)

    noFollowUps(state.copy(updatedData = updatedData))
}

fun initBodies(state: EcsState, updatedData: MutableList<Any>) {
    val map = state.getData<GameMap>()
    val bodyStorage = state.getStorage<Body>()

    val mapBuilder = map.builder()

    for (id in bodyStorage.getIds()) {
        val body = bodyStorage.getOrThrow(id)
        addToMap(mapBuilder, id, body)
    }

    updatedData += mapBuilder.build()
}

fun initMessageLog(state: EcsState, updatedData: MutableList<Any>) {
    val messageLog = state.getData<MessageLog>()
    updatedData += messageLog.add(inform("Init game"))
}

fun initTime(state: EcsState, updatedData: MutableList<Any>, speed: Skill) {
    val system = state.getOptionalData<TimeSystem>()

    if (system != null) {
        val controllerStorage = state.getStorage<Controller>()
        val newSystem = system.add(controllerStorage.getIds().sorted())
        updatedData += newSystem
        updatedData += createTurnData(state, newSystem, speed)
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