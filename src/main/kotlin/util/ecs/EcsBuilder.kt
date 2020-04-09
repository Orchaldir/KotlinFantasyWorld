package util.ecs

import util.ecs.storage.ComponentMap
import util.ecs.storage.ComponentStorage
import kotlin.reflect.KClass

class EcsBuilder(
    private val entityIds: MutableSet<Int>,
    private val storageMap: MutableMap<KClass<*>, ComponentStorage<*>>
) {
    var entityId = getFirstFreeId()
        private set

    constructor() : this(mutableSetOf(), mutableMapOf())

    fun <T> register(type: KClass<*>): EcsBuilder {
        storageMap[type] = ComponentMap<T>(mapOf())
        return this
    }

    inline fun <reified T : Any> register(): EcsBuilder {
        return register<T>(T::class)
    }

    fun newEntity(): EcsBuilder {
        entityIds.add(entityId)
        entityId = getFirstFreeId()
        return this
    }

    fun <T> add(type: KClass<*>, component: T): EcsBuilder {
        @Suppress("UNCHECKED_CAST")
        val storage =
            storageMap[type] as ComponentStorage<T>? ?: throw IllegalArgumentException("Type '$type' is unregistered!")
        storageMap[type] = storage.updateAndRemove(mapOf(entityId to component))
        return this
    }

    inline fun <reified T : Any> add(component: T): EcsBuilder {
        return add(T::class, component)
    }

    private fun getFirstFreeId(): Int {
        val maxId = entityIds.max() ?: 0

        for (i in 0..maxId) {
            if (!entityIds.contains(i)) {
                return i
            }
        }

        return maxId + 1
    }

    fun build(): EcsState {
        return EcsState(entityIds, storageMap)
    }

}