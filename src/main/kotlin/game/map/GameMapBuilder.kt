package game.map

import util.math.Size

class GameMapBuilder(
    val size: Size,
    private val terrainList: MutableList<Terrain>
) {
    constructor(size: Size, terrain: Terrain) :
            this(size, MutableList(size.getCells()) { terrain })

    constructor(x: Int, y: Int, terrain: Terrain) :
            this(Size(x, y), terrain)

    fun build(): GameMap = GameMap(size, terrainList)

    fun addBorder(terrain: Terrain): GameMapBuilder {
        return addRectangle(0, 0, size.x, size.y, terrain)
    }

    fun addRectangle(startX: Int, startY: Int, sizeX: Int, sizeY: Int, terrain: Terrain): GameMapBuilder {
        val endX = startX + sizeX
        val endY = startY + sizeY

        for (x in startX until endX) {
            terrainList[size.getIndex(x, startY)] = terrain
            terrainList[size.getIndex(x, endY - 1)] = terrain
        }

        for (y in startY until endY) {
            terrainList[size.getIndex(startX, y)] = terrain
            terrainList[size.getIndex(endX - 1, y)] = terrain
        }

        return this
    }

    fun getTerrainList(): List<Terrain> = terrainList

    fun getTerrain(x: Int, y: Int): Terrain {
        return terrainList[size.getIndex(x, y)]
    }

    fun getTerrain(index: Int): Terrain {
        return terrainList[index]
    }

    fun setTerrain(x: Int, y: Int, terrain: Terrain): GameMapBuilder {
        terrainList[size.getIndex(x, y)] = terrain
        return this
    }
}