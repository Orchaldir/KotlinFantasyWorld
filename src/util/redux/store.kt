package util.redux

class DefaultStore<Action, State>(
    initialState: State,
    private val reducer: Reducer<Action, State>
) : Store<Action, State> {

    private val subscribers = mutableSetOf<StoreSubscriber<State>>()

    private var state: State = initialState
        set(value) {
            field = value
            subscribers.forEach { it(value) }
        }

    override fun getState(): State = state

    override fun dispatch(action: Action) {
        state = reducer(state, action)
    }

    override fun subscribe(subscriber: StoreSubscriber<State>): Boolean = subscribers.add(subscriber)

}
