package ai.behavior.bt

interface Behavior<A, S> {

    fun execute(state: S, blackboard: Blackboard): Status<A>

}