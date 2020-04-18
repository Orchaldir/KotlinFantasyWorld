package game.component

sealed class Controller
object AI : Controller()
object Player : Controller()