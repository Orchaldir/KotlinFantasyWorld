package ai.behavior.bt

import mu.KotlinLogging
import util.ecs.getType
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

data class Blackboard(private val dataMap: MutableMap<String, Any>) {

    constructor() : this(mutableMapOf())

    fun <T> get(kClass: KClass<*>, key: String): T {
        val type = getType(kClass)
        val data = dataMap[key]

        @Suppress("UNCHECKED_CAST")
        return data as T? ?: throw NoSuchElementException("No data of type $type for '$key'!")
    }

    inline fun <reified T : Any> get(key: String): T = get(T::class, key)

    fun <T : Any> put(key: String, data: T) {
        logger.debug("Put '$data' for '$key'")
        dataMap[key] = data
    }
}