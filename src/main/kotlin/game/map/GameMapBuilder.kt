package game.map

import util.math.rectangle.Size

class GameMapBuilder(
    val size: Size,
    private val terrainList: MutableList<Terrain>,
    private val entities: MutableMap<Int, Int>
) {
    constructor(size: Size, terrain: Terrain) :
            this(size, MutableList(size.cells) { terrain }, mutableMapOf())

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

    fun setTerrain(index: Int, terrain: Terrain): GameMapBuilder {
        terrainList[index] = terrain
        return this
    }

    // entities

    fun setEntity(index: Int, entity: Int): GameMapBuilder {
        val overwritten = entities.put(index, entity)

        if (overwritten != null && overwritten != entity) {
            throw IllegalArgumentException("Overwritten entity $overwritten with $entity at index $index!")
        }

        return this
    }

    fun setEntity(index: Int, entity: Int, size: Int): GameMapBuilder {
        val indices = this.size.getIndices(index, size)

        if (indices.isEmpty()) {
            throw IllegalArgumentException("Can not set entity $entity at index $index with size $size!")
        }

        indices.forEach { i -> setEntity(i, entity) }

        return this
    }

    fun getEntity(index: Int) = entities[index]

    fun removeEntity(index: Int, entity: Int): GameMapBuilder {
        val removed = entities.remove(index)

        if (removed != entity) {
            throw IllegalArgumentException("Removed entity $removed instead of $entity at index $index!")
        }

        return this
    }

    fun removeEntity(index: Int, entity: Int, size: Int): GameMapBuilder {
        val indices = this.size.getIndices(index, size)

        if (indices.isEmpty()) {
            throw IllegalArgumentException("Can not remove entity $entity at index $index with size $size!")
        }

        indices.forEach { i -> removeEntity(i, entity) }

        return this
    }
}