package util

fun requireGreater(value: Int, threshold: Int, name: String): Int {
    require(value > threshold) { "Value $name requires $value > $threshold!" }
    return value
}