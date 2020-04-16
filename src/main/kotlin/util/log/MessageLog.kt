package util.log

data class MessageLog(val messages: List<Message>) {

    constructor() : this(emptyList())

    fun add(message: Message) = MessageLog(messages + message)

}