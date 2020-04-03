package util.redux

class DefaultStore<Action, State>(
    initialState: State,
    reducer: Reducer<Action, State>
) : Store<Action, State> {

    private val subscribers = mutableSetOf<StoreSubscriber<State>>()
    private val dispatcher: Dispatcher<Action> = { action -> state = reducer(state, action) }

    private var state: State = initialState
        set(value) {
            field = value
            subscribers.forEach { it(value) }
        }

    override fun getState(): State = state

    override fun dispatch(action: Action) {
        dispatcher(action)
    }

    override fun subscribe(subscriber: StoreSubscriber<State>): Boolean = subscribers.add(subscriber)

}
