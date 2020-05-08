package util.math

data class Polygon(val points: List<Point>) {

    fun rotateClockwise() = Polygon(points.map(Point::rotateClockwise))

}

fun createPolygon(vararg values: Double): Polygon {
    require(values.size >= 6) { "Polygons require an even number of points!" }
    require(values.size % 2 == 0) { "Polygons require an even number of points!" }

    val points = values
        .toList()
        .chunked(2) { list -> Point(list[0], list[1]) }

    return Polygon(points)
}