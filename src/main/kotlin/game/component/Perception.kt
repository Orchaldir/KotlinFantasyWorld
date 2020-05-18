package game.component

data class Perception(val maxRange: Int, val visibleTiles: Set<Int>, val knownTiles: Set<Int>) {

    constructor(maxRange: Int) : this(maxRange, setOf(), setOf())

    fun update(visibleTiles: Set<Int>): Perception {
        return Perception(maxRange, visibleTiles, knownTiles + visibleTiles)
    }

}