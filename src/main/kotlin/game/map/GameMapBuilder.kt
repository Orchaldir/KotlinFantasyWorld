package game.map

import util.math.Size

class GameMapBuilder(
    val size: Size,
    private val terrainList: MutableList<Terrain>,
    private val entities: MutableMap<Int, Int>
) {
    constructor(size: Size, terrain: Terrain) :
            this(size, MutableList(size.getCells()) { terrain }, mutableMapOf())

    constructor(x: Int, y: Int, terrain: Terrain) :
            this(Size(x, y), terrain)

    fun build(): GameMap = GameMap(size, terrainList, entities)

    // terrain

    fun addBorder(terrain: Terrain) = addRectangle(0, 0, size.x, size.y, terrain)

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

    fun getTerrainList() = terrainList

    fun getTerrain(x: Int, y: Int) = terrainList[size.getIndex(x, y)]

    fun getTerrain(index: Int): Terrain {
        return terrainList[index]
    }

    fun setTerrain(x: Int, y: Int, terrain: Terrain): GameMapBuilder {
        terrainList[size.getIndex(x, y)] = terrain
        return this
    }

    // entities

    fun setEntity(x: Int, y: Int, entity: Int): GameMapBuilder {
        entities[size.getIndex(x, y)] = entity
        return this
    }
}