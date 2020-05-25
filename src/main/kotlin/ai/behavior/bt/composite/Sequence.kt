package ai.behavior.bt.composite

import ai.behavior.bt.Behavior
import ai.behavior.bt.Blackboard
import ai.behavior.bt.Status
import ai.behavior.bt.Success

data class Sequence<A, S>(val behaviors: List<Behavior<A, S>>) : Behavior<A, S> {

    override fun execute(state: S, blackboard: Blackboard): Status<A> {
        for (behavior in behaviors) {
            val status = behavior.execute(state, blackboard)

            if (status !is Success) {
                return status
            }
        }

        return Success()
    }

}