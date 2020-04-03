package util.redux

class DefaultStore<Action, State>(
    initialState: State,
    reducer: Reducer<Action, State>,
    middlewareList: List<Middleware<Action, State>>
) : Store<Action, State> {

    private val subscribers = mutableSetOf<StoreSubscriber<State>>()
    private val dispatcher: Dispatcher<Action>

    init {
        var wrapped: Dispatcher<Action> = { action -> state = reducer(state, action) }

        middlewareList.forEach {
            wrapped = it(wrapped) { state }
        }

        dispatcher = wrapped
    }

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
