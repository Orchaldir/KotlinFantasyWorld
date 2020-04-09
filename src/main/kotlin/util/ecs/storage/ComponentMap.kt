package util.ecs.storage

data class ComponentMap<T>(
    val componentMap: Map<Int, T>
) : ComponentStorage<T> {

    override fun has(entityId: Int): Boolean {
        return componentMap.containsKey(entityId)
    }

    override fun get(entityId: Int): T? {
        return componentMap[entityId]
    }

    override fun getAll(): Collection<T> {
        return componentMap.values
    }

    override fun getIds(): Set<Int> {
        return componentMap.keys
    }
}