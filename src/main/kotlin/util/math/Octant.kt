package util.math

// Name is always the start of the octant. So NORTH is from north to northeast.
enum class Octant {
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;
}

fun Octant.getGlobal(originX: Int, originY: Int, x: Int, y: Int) = when (this) {
    Octant.NORTH -> Pair(originX + y, originY + x)
    Octant.NORTH_EAST -> Pair(originX + x, originY + y)
    Octant.EAST -> Pair(originX + x, originY - y)
    Octant.SOUTH_EAST -> Pair(originX + y, originY - x)
    Octant.SOUTH -> Pair(originX - y, originY - x)
    Octant.SOUTH_WEST -> Pair(originX - x, originY - y)
    Octant.WEST -> Pair(originX - x, originY + y)
    Octant.NORTH_WEST -> Pair(originX - y, originY + x)
}