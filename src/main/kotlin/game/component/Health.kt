package game.component

enum class HealthState {
    HEALTHY,
    REELING,
    NEAR_COLLAPSE,
    NEAR_DEATH,
    DEAD;

    fun getWorse() = when (this) {
        HEALTHY -> REELING
        REELING -> NEAR_COLLAPSE
        NEAR_COLLAPSE -> NEAR_DEATH
        NEAR_DEATH -> DEAD
        DEAD -> DEAD
    }

    fun getWorse(steps: Int): HealthState {
        var state = this

        repeat(steps) {
            state = state.getWorse()
        }

        return state
    }
}

data class Health(
    val state: HealthState,
    val penalty: Int
)