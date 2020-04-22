package util.ecs.storage

interface ComponentStorage<T> {
    fun getType(): String
    fun has(entity: Int): Boolean
    operator fun get(entity: Int): T?
    fun getOrThrow(entity: Int): T
    fun getList(vararg entities: Int): List<T>
    fun getAll(): Collection<T>
    fun getIds(): Set<Int>
    fun updateAndRemove(updated: Map<Int, T> = emptyMap(), removed: Set<Int> = emptySet()): ComponentStorage<T>
}