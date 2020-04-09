package util.ecs.storage

interface ComponentStorage<T> {
    fun has(entityId: Int): Boolean
    operator fun get(entityId: Int): T?
    fun getAll(): Collection<T>
    fun getIds(): Set<Int>
}