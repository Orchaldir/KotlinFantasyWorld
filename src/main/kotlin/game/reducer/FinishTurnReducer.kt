package game.reducer

import game.FinishTurnAction
import game.rpg.character.skill.SkillUsage
import game.rpg.time.TimeSystem
import game.rpg.time.TurnData
import game.rpg.time.createTurnData
import util.ecs.EcsState
import util.redux.Reducer

val FINISH_TURN_REDUCER: Reducer<FinishTurnAction, EcsState> = { state, action ->
    val skillUsage = state.getData<SkillUsage>()
    val newTimeSystem = state.getData<TimeSystem>().finishTurn(action.entity)
    val newTurnData = createTurnData(state, newTimeSystem, skillUsage.speed)
    state.copy(updatedDataMap = mapOf(TimeSystem::class to newTimeSystem, TurnData::class to newTurnData))
}