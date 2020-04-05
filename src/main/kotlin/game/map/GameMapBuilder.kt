package game.map

import util.requireGreater

class GameMapBuilder(
    sizeX: Int,
    sizeY: Int,
    private val terrainList: MutableList<Terrain>
) {
    private val sizeX = requireGreater(sizeX, 0, "sizeX")
    private val sizeY = requireGreater(sizeY, 0, "sizeY")

    constructor(sizeX: Int, sizeY: Int, terrain: Terrain) :
            this(sizeX, sizeY, MutableList(sizeX * sizeY) { terrain })

    fun build(): GameMap = GameMap(sizeX, sizeY, terrainList)
}