package ai.behavior.bt

sealed class Status<A>
data class PerformAction<A>(val action: A) : Status<A>()
class Success<A> : Status<A>()
class Failure<A> : Status<A>()