package util.math.fov

interface FovAlgorithm {

    fun calculateVisibleCells(config: FovConfig): Set<Int>

}