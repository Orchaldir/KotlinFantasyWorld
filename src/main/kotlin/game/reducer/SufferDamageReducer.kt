package game.reducer

import game.SufferDamageAction
import game.component.Health
import game.component.Statistics
import util.ecs.EcsState
import util.redux.Reducer

val SUFFER_DAMAGE_REDUCER: Reducer<SufferDamageAction, EcsState> = { state, action ->
    val healthStorage = state.get<Health>() ?: throw IllegalStateException("No health storage!")
    val health = healthStorage[action.entity] ?: throw IllegalStateException("Entity ${action.entity} has no health!")

    val statisticsStorage = state.get<Statistics>() ?: throw IllegalStateException("No statistics storage!")
    val statistics =
        statisticsStorage[action.entity] ?: throw IllegalStateException("Entity ${action.entity} has no statistics!")

    state
}
