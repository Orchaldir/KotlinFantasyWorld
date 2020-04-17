package game.reducer

import game.FinishTurnAction
import game.rpg.time.TimeSystem
import util.ecs.EcsState
import util.redux.Reducer

val FINISH_TURN_REDUCER: Reducer<FinishTurnAction, EcsState> = { state, action ->
    val newTimeSystem = state.getData<TimeSystem>().finishTurn(action.entity)
    state.copy(updatedDataMap = mapOf(TimeSystem::class to newTimeSystem))
}