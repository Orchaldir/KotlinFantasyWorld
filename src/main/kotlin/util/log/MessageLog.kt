package util.log

import util.ecs.EcsState

data class MessageLog(val messages: List<Message>) {

    constructor() : this(emptyList())

    fun add(message: Message) = MessageLog(messages + message)

}

fun addMessage(state: EcsState, message: Message): EcsState {
    val messageLog = state.getData<MessageLog>().add(message)
    return state.copy(updatedData = listOf(messageLog))
}