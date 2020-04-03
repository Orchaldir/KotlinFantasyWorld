package util.redux

import kotlin.test.Test
import kotlin.test.assertEquals

class StoreTest {
    @Test
    fun testGetInitialState() {
        val store = createStore()
        assertEquals(store.getState(), 10)
    }

    @Test
    fun testDispatch() {
        val store = createStore()
        store.dispatch(3)
        assertEquals(store.getState(), 13)
    }

    @Test
    fun testOneSubscriber() {
        val store = createStore()
        var calls = 0
        val stateList = mutableListOf<Int>()

        store.subscribe { new ->
            calls++
            stateList.add(new)
        }

        store.dispatch(5)
        store.dispatch(-1)

        assertEquals(2, calls)
        assertEquals(listOf(15, 14), stateList)
    }

    private fun createStore() = DefaultStore<Int, Int>(10, { state, action -> state + action })
}