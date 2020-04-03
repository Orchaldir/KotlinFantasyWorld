package util.redux

import kotlin.test.Test
import kotlin.test.assertEquals

class StoreTest {
    @Test fun testGetInitialState() {
        val store = DefaultStore<Int, Int>(10, { state, action -> state + action })
        assertEquals(store.getState(), 10)
    }
}
