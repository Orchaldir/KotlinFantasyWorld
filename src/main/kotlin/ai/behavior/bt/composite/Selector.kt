package ai.behavior.bt.composite

import ai.behavior.bt.Behavior
import ai.behavior.bt.Failure
import ai.behavior.bt.Status

data class Selector<A, S>(val behaviors: List<Behavior<A, S>>) : Behavior<A, S> {

    override fun execute(state: S): Status<A> {
        for (behavior in behaviors) {
            val status = behavior.execute(state)

            if (status !is Failure) {
                return status
            }
        }

        return Failure()
    }

}