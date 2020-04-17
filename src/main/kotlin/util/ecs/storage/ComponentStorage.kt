package util.ecs.storage

interface ComponentStorage<T> {
    fun getType(): String
    fun has(entityId: Int): Boolean
    operator fun get(entityId: Int): T?
    fun getOrThrow(entityId: Int): T
    fun getAll(): Collection<T>
    fun getIds(): Set<Int>
    fun updateAndRemove(updated: Map<Int, T>, removed: Set<Int> = emptySet()): ComponentStorage<T>
}