package ai.behavior.bt.condition

data class And<S>(private val name: String, val conditions: List<Condition<S>>) : Condition<S> {

    constructor(conditions: List<Condition<S>>) : this(
        conditions.joinToString(prefix = "And(", postfix = ")", transform = Condition<S>::getName),
        conditions
    )

    override fun check(state: S): Boolean {
        for (condition in conditions) {
            if (!condition.check(state)) {
                return false
            }
        }

        return true
    }

    override fun getName() = name

}