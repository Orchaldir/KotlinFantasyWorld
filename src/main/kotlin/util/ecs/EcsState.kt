package util.ecs

import util.ecs.storage.ComponentStorage
import kotlin.reflect.KClass

class EcsState(
    val entityIds: Set<Int>,
    private val storageMap: Map<KClass<*>, ComponentStorage<*>>,
    private val dataMap: Map<KClass<*>, Any>
) {

    constructor(storageMap: Map<KClass<*>, ComponentStorage<*>>) : this(mutableSetOf(), storageMap, mapOf())

    fun <T> get(type: KClass<*>): ComponentStorage<T>? {
        val storage = storageMap[type]

        @Suppress("UNCHECKED_CAST")
        return storage as ComponentStorage<T>?
    }

    inline fun <reified T : Any> get(): ComponentStorage<T>? = get(T::class)

    fun <T> getData(type: KClass<*>): T? {
        val data = dataMap[type]

        @Suppress("UNCHECKED_CAST")
        return data as T?
    }

    inline fun <reified T : Any> getData(): T? = getData(T::class)

    fun copy(updated: Map<KClass<*>, ComponentStorage<*>>): EcsState {
        val newStorageMap = storageMap + updated
        return EcsState(entityIds, newStorageMap, dataMap)
    }

}