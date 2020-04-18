package game.rpg.time

data class TimeSystem(
    val turn: Int = 0,
    private val entities: List<Int> = emptyList(),
    private val finished: List<Int> = emptyList()
) {

    fun add(entity: Int) = copy(entities = entities + entity)

    fun add(newEntities: List<Int>) = copy(entities = entities + newEntities)

    fun remove(entity: Int) = TimeSystem(turn, entities - entity, finished - entity)

    fun getCurrent() = entities.first()

    fun validateCurrent(entity: Int) {
        val current = entities.firstOrNull()

        if (entity != current) {
            throw NotCurrentEntityException(entity, current ?: -1)
        }
    }

    fun finishTurn(entity: Int): TimeSystem {
        validateCurrent(entity)
        val newEntities = entities - entity

        return if (newEntities.isEmpty()) {
            TimeSystem(turn + 1, finished + entity, emptyList())
        } else {
            TimeSystem(turn, newEntities, finished + entity)
        }
    }

}