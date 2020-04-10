package game.map

enum class Terrain {
    FLOOR,
    WALL;

    fun isWalkable() = this == FLOOR
}