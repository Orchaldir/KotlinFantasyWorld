package app.demo

import util.redux.DefaultStore
import util.redux.middleware.logAction
import util.redux.noFollowUps

fun main() {
    println("Hello World!")
    val store = DefaultStore<Int, Int>(10, { state, action -> noFollowUps(state + action) }, listOf(::logAction))
    store.subscribe { state -> println("state=$state") }
    store.dispatch(2)
}