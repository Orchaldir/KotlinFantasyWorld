package game.rpg.check


sealed class CheckResult
object CriticalSuccess : CheckResult()
data class Success(val rank: Int) : CheckResult()
object Draw : CheckResult()
data class Failure(val rank: Int) : CheckResult()
object CriticalFailure : CheckResult()