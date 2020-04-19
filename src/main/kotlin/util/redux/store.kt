package util.redux

class DefaultStore<Action, State>(
    private var state: State,
    reducer: Reducer<Action, State>,
    middlewareList: List<Middleware<Action, State>>
) : Store<Action, State> {

    private val subscribers = mutableSetOf<StoreSubscriber<State>>()
    private val dispatcher: Dispatcher<Action>

    init {
        var wrapped: Dispatcher<Action> = { action ->
            val actions = mutableListOf(action)

            while (actions.isNotEmpty()) {
                val result = reducer(state, actions.removeAt(0))

                state = result.first
                actions += result.second
            }
        }

        middlewareList.forEach {
            wrapped = it(wrapped) { state }
        }

        dispatcher = wrapped
    }

    override fun getState(): State = state

    override fun dispatch(action: Action) {
        dispatcher(action)
        subscribers.forEach { it(state) }
    }

    override fun subscribe(subscriber: StoreSubscriber<State>): Boolean = subscribers.add(subscriber)

}
