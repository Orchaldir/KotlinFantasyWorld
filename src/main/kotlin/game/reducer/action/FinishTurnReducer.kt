package game.reducer.action

import game.action.FinishTurn
import game.rpg.character.skill.SkillUsage
import game.rpg.time.TimeSystem
import game.rpg.time.createTurnData
import util.ecs.EcsState
import util.redux.Reducer
import util.redux.noFollowUps

val FINISH_TURN_REDUCER: Reducer<FinishTurn, EcsState> = { state, action ->
    val skillUsage = state.getData<SkillUsage>()
    val newTimeSystem = state.getData<TimeSystem>().finishTurn(action.entity)
    val newTurnData = createTurnData(state, newTimeSystem, skillUsage.speed)

    noFollowUps(state.copy(updatedData = listOf(newTimeSystem, newTurnData)))
}