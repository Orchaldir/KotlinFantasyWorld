package util.ecs.storage

data class ComponentMap<T>(
    private val type: String,
    val componentMap: Map<Int, T>
) : ComponentStorage<T> {

    override fun getType() = type

    override fun has(entity: Int) = componentMap.containsKey(entity)

    override fun get(entity: Int) = componentMap[entity]

    override fun getOrThrow(entity: Int): T {
        return componentMap[entity] ?: throw NoSuchElementException("Entity $entity has no $type!")
    }

    override fun getList(vararg entities: Int) = entities.map { getOrThrow(it) }.toList()

    override fun getAll(): Collection<T> = componentMap.values

    override fun getIds() = componentMap.keys

    override fun updateAndRemove(updated: Map<Int, T>, removed: Set<Int>): ComponentStorage<T> {
        val newComponentMap = componentMap + updated - removed
        return copy(componentMap = newComponentMap)
    }
}