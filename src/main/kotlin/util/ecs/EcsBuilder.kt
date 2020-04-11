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
    private var hasComponents = false

    constructor() : this(mutableSetOf(), mutableMapOf(), mutableMapOf())

    fun <T> register(type: KClass<*>) {
        storageMap[type] = ComponentMap<T>(mapOf())
    }

    inline fun <reified T : Any> register() = register<T>(T::class)

    fun buildEntity(): Int {
        logger.info("Add entity $entityId")
        val lastId = entityId
        entityIds.add(entityId)
        entityId = getFirstFreeId()
        hasComponents = false
        return lastId
    }

    fun <T> add(type: KClass<*>, component: T) {
        hasComponents = true
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
        dataMap[type] = data
    }

    inline fun <reified T : Any> addData(data: T) = addData(T::class, data)

    fun build(): EcsState {
        if (hasComponents) {
            logger.warn("Did not call buildEntity() for entity $entityId!")
            entityIds.add(entityId)
        }
        return EcsState(entityIds, storageMap, dataMap)
    }

}