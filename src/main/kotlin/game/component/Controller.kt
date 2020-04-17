package game.component

sealed class Controller
object Ai : Controller()
object Player : Controller()