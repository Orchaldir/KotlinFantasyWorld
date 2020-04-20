package game.reducer.action

import game.action.AddMessage
import util.ecs.EcsState
import util.log.addMessage
import util.redux.Reducer
import util.redux.noFollowUps

val ADD_MESSAGE_REDUCER: Reducer<AddMessage, EcsState> = { state, action ->
    noFollowUps(addMessage(state, action.message))
}