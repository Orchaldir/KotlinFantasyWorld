package game.map

import util.requireGreater

class GameMap(
    sizeX: Int,
    sizeY: Int,
    private val terrainList: List<Terrain>
) {
    private val sizeX = requireGreater(sizeX, 0, "sizeX")
    private val sizeY = requireGreater(sizeY, 0, "sizeY")
}