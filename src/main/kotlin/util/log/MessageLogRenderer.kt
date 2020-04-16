package util.log

import util.rendering.tile.TileRenderer

class MessageLogRenderer(val x: Int, val y: Int, val size: Int = 1) {

    fun render(tileRenderer: TileRenderer, messageLog: MessageLog) {
        with(tileRenderer) {
            for ((i, message) in messageLog.messages.reversed().withIndex()) {
                renderText(message.text, message.color, x, y + i * size, size)
            }
        }
    }

}