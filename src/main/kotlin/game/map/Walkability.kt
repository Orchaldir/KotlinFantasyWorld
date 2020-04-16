package game.map

sealed class Walkability
data class Walkable(val position: Int) : Walkability()
object BlockedByObstacle : Walkability()
data class BlockedByEntity(val entity: Int) : Walkability()
object OutsideMap : Walkability()

infix fun Int?.then(f: (Int) -> Walkability) =
    when (this) {
        null -> OutsideMap
        else -> f(this)
    }