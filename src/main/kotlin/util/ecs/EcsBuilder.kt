package util.ecs

import mu.KotlinLogging
import util.ecs.storage.ComponentMap
import util.ecs.storage.ComponentStorage
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

class EcsBuilder(
    private val entities: MutableSet<Int>,
    private val storageMap: MutableMap<String, ComponentStorage<*>>,
    private val dataMap: MutableMap<String, Any>
) {
    var entity = getFirstFreeId()
        private set
    private var numberOfComponents = 0

    constructor() : this(mutableSetOf(), mutableMapOf(), mutableMapOf())

    fun <T> registerComponent(kClass: KClass<*>) {
        val type = getType(kClass)
        logger.info("Register component $type")
        storageMap[type] = ComponentMap<T>(type, mapOf())
    }

    inline fun <reified T : Any> registerComponent() = registerComponent<T>(T::class)

    fun buildEntity(): Int {
        logger.info("Add entity $entity with $numberOfComponents components")
        val lastId = entity
        entities.add(entity)
        entity = getFirstFreeId()
        numberOfComponents = 0
        return lastId
    }

    fun <T> add(kClass: KClass<*>, component: T) {
        val type = getType(kClass)
        numberOfComponents++
        @Suppress("UNCHECKED_CAST")
        val storage =
            storageMap[type] as ComponentStorage<T>? ?: throw IllegalArgumentException("Type '$type' is unregistered!")
        storageMap[type] = storage.updateAndRemove(mapOf(entity to component))
    }

    inline fun <reified T : Any> add(component: T) = add(T::class, component)

    private fun getFirstFreeId(): Int {
        val maxId = entities.max() ?: 0

        for (i in 0..maxId) {
            if (!entities.contains(i)) {
                return i
            }
        }

        return maxId + 1
    }

    // data

    fun <T : Any> addData(kClass: KClass<*>, data: T) {
        val type = getType(kClass)
        logger.info("Add data for $type")
        dataMap[type] = data
    }

    inline fun <reified T : Any> addData(data: T) = addData(T::class, data)

    fun build(): EcsState {
        if (numberOfComponents > 0) {
            logger.warn("Did not call buildEntity() for entity $entity!")
            entities.add(entity)
        }
        return EcsState(entities, storageMap, dataMap)
    }

}