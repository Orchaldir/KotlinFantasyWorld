package app.demo

import util.redux.DefaultStore

fun main() {
    println("Hello World!")
    val store = DefaultStore<Int, Int>(10, { state, action -> state + action }, listOf())
    store.subscribe { state -> println("state=$state") }
    store.dispatch(2)
}