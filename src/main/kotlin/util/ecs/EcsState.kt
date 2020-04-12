package util.ecs

import util.ecs.storage.ComponentStorage
import kotlin.reflect.KClass

class EcsState(
    val entityIds: Set<Int> = emptySet(),
    private val storageMap: Map<KClass<*>, ComponentStorage<*>> = emptyMap(),
    private val dataMap: Map<KClass<*>, Any> = emptyMap()
) {

    fun <T> getStorage(type: KClass<*>): ComponentStorage<T> {
        val storage = storageMap[type]

        @Suppress("UNCHECKED_CAST")
        return storage as ComponentStorage<T>? ?: throw IllegalArgumentException("No storage for $type!")
    }

    inline fun <reified T : Any> getStorage(): ComponentStorage<T> = getStorage(T::class)

    fun <T> getData(type: KClass<*>): T? {
        val data = dataMap[type]

        @Suppress("UNCHECKED_CAST")
        return data as T?
    }

    inline fun <reified T : Any> getData(): T? = getData(T::class)

    fun copy(
        updatedStorageMap: Map<KClass<*>, ComponentStorage<*>> = emptyMap(),
        updatedDataMap: Map<KClass<*>, Any> = emptyMap()
    ): EcsState {
        val newStorageMap = if (updatedStorageMap.isEmpty()) {
            storageMap
        } else {
            storageMap + updatedStorageMap
        }

        val newDataMap = if (updatedDataMap.isEmpty()) {
            dataMap
        } else {
            dataMap + updatedDataMap
        }

        return EcsState(entityIds, newStorageMap, newDataMap)
    }

}