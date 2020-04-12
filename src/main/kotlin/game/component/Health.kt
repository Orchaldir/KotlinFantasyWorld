package game.component

enum class HealthState {
    HEALTHY,
    REELING,
    DEAD;

    fun getWorse() = when (this) {
        HEALTHY -> REELING
        REELING -> DEAD
        DEAD -> DEAD
    }
}

data class Health(
    val state: HealthState,
    val penalty: Int
)