package game.reducer

import game.SufferDamageAction
import game.component.Health
import game.component.Statistics
import util.ecs.EcsState
import util.redux.Reducer

val SUFFER_DAMAGE_REDUCER: Reducer<SufferDamageAction, EcsState> = { state, action ->
    val healthStorage = state.getStorage<Health>()
    val health = healthStorage.getOrThrow(action.entity)

    val statisticsStorage = state.getStorage<Statistics>()
    val statistics = statisticsStorage.getOrThrow(action.entity)

    state
}
