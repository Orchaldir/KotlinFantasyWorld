package util.math.rectangle

import kotlin.math.absoluteValue
import kotlin.math.max

sealed class DistanceCalculator
object Chebyshev : DistanceCalculator()
object Manhattan : DistanceCalculator()


fun calculateDistance(calculator: DistanceCalculator, fromX: Int, fromY: Int, toX: Int, toY: Int) = when (calculator) {
    Chebyshev -> max((toX - fromX).absoluteValue, (toY - fromY).absoluteValue)
    Manhattan -> (toX - fromX).absoluteValue + (toY - fromY).absoluteValue
}