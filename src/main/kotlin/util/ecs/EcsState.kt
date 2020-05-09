package util.ecs

import util.ecs.storage.ComponentStorage
import kotlin.reflect.KClass

class EcsState(
    val entities: Set<Int> = emptySet(),
    private val storageMap: Map<String, ComponentStorage<*>> = emptyMap(),
    private val dataMap: Map<String, Any> = emptyMap()
) {

    fun <T> getStorage(kClass: KClass<*>): ComponentStorage<T> {
        val type = getType(kClass)
        val storage = storageMap[type]

        @Suppress("UNCHECKED_CAST")
        return storage as ComponentStorage<T>? ?: throw NoSuchElementException("No storage for $type!")
    }

    inline fun <reified T : Any> getStorage(): ComponentStorage<T> = getStorage(T::class)

    fun <T> getOptionalStorage(kClass: KClass<*>): ComponentStorage<T>? {
        val type = getType(kClass)
        val storage = storageMap[type]

        @Suppress("UNCHECKED_CAST")
        return storage as ComponentStorage<T>?
    }

    inline fun <reified T : Any> getOptionalStorage(): ComponentStorage<T>? = getOptionalStorage(T::class)

    fun <T> getData(kClass: KClass<*>): T {
        val type = getType(kClass)
        val data = dataMap[type]

        @Suppress("UNCHECKED_CAST")
        return data as T? ?: throw NoSuchElementException("No data for $type!")
    }

    inline fun <reified T : Any> getData(): T = getData(T::class)

    fun <T> getOptionalData(kClass: KClass<*>): T? {
        val type = getType(kClass)
        val data = dataMap[type]

        @Suppress("UNCHECKED_CAST")
        return data as T?
    }

    inline fun <reified T : Any> getOptionalData(): T? = getOptionalData(T::class)

    fun copy(
        updatedStorage: List<ComponentStorage<*>> = emptyList(),
        updatedData: List<Any> = emptyList()
    ): EcsState {
        val newStorageMap = if (updatedStorage.isEmpty()) {
            storageMap
        } else {
            storageMap + updatedStorage.map { it.getType() to it }
        }

        val newDataMap = if (updatedData.isEmpty()) {
            dataMap
        } else {
            dataMap + updatedData.map { getType(it::class) to it }.toMap()
        }

        return EcsState(entities, newStorageMap, newDataMap)
    }

}