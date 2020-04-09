package util.ecs

import util.ecs.storage.ComponentStorage
import kotlin.reflect.KClass

class EcsState(private val componentMap: Map<KClass<*>, ComponentStorage<*>>) {

    fun <T> get(type: KClass<*>): ComponentStorage<T>? {
        val storage = componentMap[type]

        @Suppress("UNCHECKED_CAST")
        return storage as ComponentStorage<T>?
    }

    inline fun <reified T : Any> get(): ComponentStorage<T>? {
        return get(T::class)
    }

}