package game.reducer.event

import game.action.OnDeath
import game.component.Controller
import game.rpg.time.TimeSystem
import mu.KotlinLogging
import util.ecs.EcsState
import util.redux.Reducer
import util.redux.noFollowUps

private val logger = KotlinLogging.logger {}

val ON_DEATH_REDUCER: Reducer<OnDeath, EcsState> = a@{ state, action ->
    val storage = state.getStorage<Controller>()
    val newStorage = storage.updateAndRemove(removed = setOf(action.entity))

    val system = state.getData<TimeSystem>()
    val newSystem = system.remove(action.entity)

    logger.info("Entity ${action.entity} died. storage={} time={}", newStorage, newSystem)
    noFollowUps(state.copy(listOf(newStorage), listOf(newSystem)))
}

