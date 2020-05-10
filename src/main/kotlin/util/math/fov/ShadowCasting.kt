package util.math.fov

import mu.KotlinLogging
import util.math.*

private val logger = KotlinLogging.logger {}

class ShadowCasting : FovAlgorithm {

    private enum class Status {
        UNDEFINED,
        BLOCKING,
        CLEAR;
    }

    override fun calculateVisibleCells(config: FovConfig): Set<Int> {
        val visibleCells = mutableSetOf(config.position)

        val top = Slope(1, 1)
        val bottom = Slope(1, 0)

        Octant.values().forEach {
            processOctant(config, visibleCells, it, 1, top, bottom)
        }

        return visibleCells
    }

    private fun processOctant(
        config: FovConfig,
        visibleCells: MutableSet<Int>,
        octant: Octant,
        startX: Int,
        parentTop: Slope,
        parentBottom: Slope
    ) {
        var top = parentTop
        var bottom = parentBottom
        logger.info("$octant startX=$startX top=$top bottom=$bottom")

        for (localX in startX until config.range) {
            val topY = top.calculateTopX(localX)
            val bottomY = bottom.calculateBottomX(localX)

            logger.info("x=$localX topY=$topY bottomY=$bottomY")

            var status = Status.UNDEFINED

            for (localY in topY downTo bottomY) {
                val (x, y) = octant.getGlobal(config.x, config.y, localX, localY)

                val index = config.mapSize.getIndex(x, y)
                visibleCells.add(index)

                val isBlocking = config.isBlocking(index)

                logger.info("  x=$localX y=$localY isBlocking=$isBlocking previous=$status")

                if (isBlocking) {
                    if (status == Status.CLEAR) {
                        val newBottom = createSlopeAboveCurrent(localX, localY)

                        if (localY == bottomY) {
                            bottom = newBottom
                            break
                        } else processOctant(
                            config, visibleCells, octant,
                            localX + 1,
                            top,
                            newBottom
                        )
                    }
                    status = Status.BLOCKING
                } else {
                    if (status == Status.BLOCKING) top = createSlopeBelowPrevious(localX, localY)

                    status = Status.CLEAR
                }
            }

            if (status != Status.CLEAR) break
        }
    }
}