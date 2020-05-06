package util.log

import util.math.rectangle.Size
import util.rendering.tile.TileRenderer

class MessageLogRenderer(val x: Int, val y: Int, val size: Size, val fontSize: Int = 1) {

    fun render(tileRenderer: TileRenderer, messageLog: MessageLog) {
        with(tileRenderer) {
            for ((i, message) in messageLog.messages.reversed().withIndex()) {
                if (i >= size.y) {
                    return
                }

                renderText(message.text, message.color, x, y + i * fontSize, fontSize)
            }
        }
    }

}