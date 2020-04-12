package util.ecs.storage

data class ComponentMap<T>(
    val type: String,
    val componentMap: Map<Int, T>
) : ComponentStorage<T> {

    override fun has(entityId: Int) = componentMap.containsKey(entityId)

    override fun get(entityId: Int) = componentMap[entityId]

    override fun getOrThrow(entityId: Int): T {
        return componentMap[entityId] ?: throw NoSuchElementException("Entity $entityId has no $type!")
    }

    override fun getAll(): Collection<T> = componentMap.values

    override fun getIds() = componentMap.keys

    override fun updateAndRemove(updated: Map<Int, T>, removed: Set<Int>): ComponentStorage<T> {
        val newComponentMap = componentMap + updated - removed
        return ComponentMap(type, newComponentMap)
    }
}