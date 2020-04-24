package util.log

import game.component.getText
import javafx.scene.paint.Color
import javafx.scene.paint.Color.WHITE
import javafx.scene.paint.Color.YELLOW
import util.ecs.EcsState

data class Message(val text: String, val color: Color)

fun inform(text: String) = Message(text.capitalize(), WHITE)

fun inform(state: EcsState, text: String, entity: Int) = inform(text.format(getText(state, entity)))

fun inform(state: EcsState, text: String, e0: Int, e1: Int) =
    inform(text.format(getText(state, e0), getText(state, e1)))

fun warn(text: String) = Message(text.capitalize(), YELLOW)

fun warn(state: EcsState, text: String, entity: Int) = warn(text.format(getText(state, entity)))

fun warn(state: EcsState, text: String, e0: Int, e1: Int) =
    warn(text.format(getText(state, e0), getText(state, e1)))