package game.rpg.time

data class TimeSystem(
    val turn: Int = 0,
    val entities: List<Int> = emptyList(),
    val finished: List<Int> = emptyList()
) {

    fun add(entity: Int) = copy(entities = entities + entity)

    fun add(newEntities: List<Int>) = copy(entities = entities + newEntities)

    fun remove(entity: Int) = TimeSystem(turn, entities - entity, finished - entity)

    fun finishTurn(entity: Int): TimeSystem {
        require(entities.firstOrNull() == entity) { "Entity $entity is not the current one!" }
        val newEntities = entities - entity

        return if (newEntities.isEmpty()) {
            TimeSystem(turn + 1, finished + entity, emptyList())
        } else {
            TimeSystem(turn, newEntities, finished + entity)
        }
    }

}