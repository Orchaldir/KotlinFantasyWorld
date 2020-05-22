package ai.behavior.bt.condition

data class Or<S>(private val name: String, val conditions: List<Condition<S>>) : Condition<S> {

    constructor(conditions: List<Condition<S>>) : this(
        conditions.joinToString(prefix = "Or(", postfix = ")", transform = Condition<S>::getName),
        conditions
    )

    override fun check(state: S): Boolean {
        for (condition in conditions) {
            if (condition.check(state)) {
                return true
            }
        }

        return false
    }

    override fun getName() = name

}