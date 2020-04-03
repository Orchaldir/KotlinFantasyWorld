package util.redux

typealias Dispatcher<Action> = (Action) -> Unit
typealias Reducer<Action, State> = (State, Action) -> State
typealias StoreSubscriber<State> = (State) -> Unit

interface Store<Action, State> {
    fun getState(): State
    fun dispatch(action: Action)
    fun subscribe(subscriber: StoreSubscriber<State>): Boolean
}