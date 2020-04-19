package util.math

data class Area(
    val x: Int,
    val y: Int,
    val size: Size
) {

    fun convert(x: Int, y: Int) = size.getIndex(x - this.x, y - this.y)

    fun getX(position: Int) = x + size.getX(position)

    fun getY(position: Int) = y + size.getY(position)

    // inside check

    fun isInside(x: Int, y: Int) = isInsideForX(x) && isInsideForY(y)

    fun isInsideForX(x: Int) = size.isInsideForX(x - this.x)

    fun isInsideForY(y: Int) = size.isInsideForY(y - this.y)
}