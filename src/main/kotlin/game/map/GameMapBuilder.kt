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

    fun addBorder(terrain: Terrain): GameMapBuilder {
        return addRectangle(0, 0, sizeX, sizeY, terrain)
    }

    fun addRectangle(startX: Int, startY: Int, sizeX: Int, sizeY: Int, terrain: Terrain): GameMapBuilder {
        val endX = startX + sizeX
        val endY = startY + sizeY

        for (x in startX until endX) {
            terrainList[getIndex(x, startY)] = terrain
            terrainList[getIndex(x, endY - 1)] = terrain
        }

        for (y in startY until endY) {
            terrainList[getIndex(startX, y)] = terrain
            terrainList[getIndex(endX - 1, y)] = terrain
        }

        return this
    }

    fun setTerrain(x: Int, y: Int, terrain: Terrain): GameMapBuilder {
        terrainList[getIndex(x, y)] = terrain
        return this
    }

    private fun getIndex(x: Int, y: Int): Int = (y * sizeX) + x
}