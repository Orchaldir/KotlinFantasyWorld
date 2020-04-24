package game.component

import util.ecs.EcsState

sealed class Text
data class Name(val name: String) : Text()
data class Description(val description: String, val text: String) : Text() {
    constructor(description: String) : this(
        description,
        when (description[0].toLowerCase()) {
            'a', 'e', 'i', 'o', 'u' -> "an $description"
            else -> "a $description"
        }
    )
}

fun getText(text: Text) = when (text) {
    is Name -> text.name
    is Description -> text.text
}

fun getText(state: EcsState, entity: Int): String {
    val text = state.getOptionalStorage<Text>()?.get(entity)

    return if (text != null) {
        getText(text)
    } else {
        "entity $entity"
    }
}