package util.ecs

import util.ecs.storage.ComponentStorage

class EcsState(private val componentMap: Map<Int, ComponentStorage<*>>) {
    fun <T> get(type: Int): ComponentStorage<T>? {
        val storage = componentMap[type]

        @Suppress("UNCHECKED_CAST")
        return storage as ComponentStorage<T>
    }
}