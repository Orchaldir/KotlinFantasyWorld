package util.redux

typealias Dispatcher<Action> = (Action) -> Unit
typealias Middleware<Action, State> = (Dispatcher<Action>, () -> State) -> Dispatcher<Action>
typealias Reducer<Action, State> = (State, Action) -> Pair<State, List<Action>>
typealias StoreSubscriber<State> = (State) -> Unit

interface Store<Action, State> {
    fun getState(): State
    fun dispatch(action: Action)
    fun subscribe(subscriber: StoreSubscriber<State>): Boolean
}

fun <Action, State> noFollowUps(state: State) = Pair(state, emptyList<Action>())