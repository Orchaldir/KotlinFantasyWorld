package util.ecs

import mu.KotlinLogging
import util.ecs.storage.ComponentMap
import util.ecs.storage.ComponentStorage
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

class EcsBuilder(
    private val entityIds: MutableSet<Int>,
    private val storageMap: MutableMap<KClass<*>, ComponentStorage<*>>,
    private val dataMap: MutableMap<KClass<*>, Any>
) {
    var entityId = getFirstFreeId()
        private set
    private var numberOfComponents = 0

    constructor() : this(mutableSetOf(), mutableMapOf(), mutableMapOf())

    fun <T> registerComponent(type: KClass<*>) {
        logger.info("Register component ${type.simpleName}")
        storageMap[type] = ComponentMap<T>(type.toString(), mapOf())
    }

    inline fun <reified T : Any> registerComponent() = registerComponent<T>(T::class)

    fun buildEntity(): Int {
        logger.info("Add entity $entityId with $numberOfComponents components")
        val lastId = entityId
        entityIds.add(entityId)
        entityId = getFirstFreeId()
        numberOfComponents = 0
        return lastId
    }

    fun <T> add(type: KClass<*>, component: T) {
        numberOfComponents++
        @Suppress("UNCHECKED_CAST")
        val storage =
            storageMap[type] as ComponentStorage<T>? ?: throw IllegalArgumentException("Type '$type' is unregistered!")
        storageMap[type] = storage.updateAndRemove(mapOf(entityId to component))
    }

    inline fun <reified T : Any> add(component: T) = add(T::class, component)

    private fun getFirstFreeId(): Int {
        val maxId = entityIds.max() ?: 0

        for (i in 0..maxId) {
            if (!entityIds.contains(i)) {
                return i
            }
        }

        return maxId + 1
    }

    // data

    fun <T : Any> addData(type: KClass<*>, data: T) {
        logger.info("Add data for ${type.simpleName}")
        dataMap[type] = data
    }

    inline fun <reified T : Any> addData(data: T) = addData(T::class, data)

    fun build(): EcsState {
        if (numberOfComponents > 0) {
            logger.warn("Did not call buildEntity() for entity $entityId!")
            entityIds.add(entityId)
        }
        return EcsState(entityIds, storageMap, dataMap)
    }

}