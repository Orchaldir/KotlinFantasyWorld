package ai.behavior.bt.condition

data class Not<S>(val condition: Condition<S>) : Condition<S> {

    override fun check(state: S) = !condition.check(state)

    override fun getName() = "!" + condition.getName()

}