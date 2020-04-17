package game.reducer

import game.FinishTurnAction
import game.rpg.character.skill.Skill
import game.rpg.time.TimeSystem
import game.rpg.time.TurnData
import game.rpg.time.createTurnData
import util.ecs.EcsState
import util.redux.Reducer

fun createFinishTurnReducer(speed: Skill): Reducer<FinishTurnAction, EcsState> = { state, action ->
    val newTimeSystem = state.getData<TimeSystem>().finishTurn(action.entity)
    val newTurnData = createTurnData(state, newTimeSystem, speed)
    state.copy(updatedDataMap = mapOf(TimeSystem::class to newTimeSystem, TurnData::class to newTurnData))
}