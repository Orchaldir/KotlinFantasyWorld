package ai.behavior.bt.condition

interface Condition<S> {

    fun check(state: S): Boolean

    fun getName(): String

}