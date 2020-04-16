package util.log

data class MessageLog(val messages: List<Message>) {

    fun add(message: Message) = MessageLog(messages + message)

}