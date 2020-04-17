package util.ecs

import assertk.all
import assertk.assertThat
import assertk.assertions.isNotSameAs
import assertk.assertions.isSameAs
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.ecs.storage.ComponentStorage
import kotlin.test.assertFailsWith

class EcsStateTest {

    private val c0 = mockk<ComponentStorage<Int>>()
    private val c1 = mockk<ComponentStorage<String>>()

    @Nested
    inner class GetStorage {

        @Test
        fun `Test getStorage() with existing type`() {
            val state = EcsState(storageMap = mapOf(Int::class to c0, String::class to c1))

            assertThat(state.getStorage<Int>()).isSameAs(c0)
            assertThat(state.getStorage<String>()).isSameAs(c1)
        }

        @Test
        fun `Test getStorage() with non-existing type`() {
            val state = EcsState(storageMap = mapOf(Int::class to c0))

            assertFailsWith<NoSuchElementException> { state.getStorage<String>() }
        }

    }

    @Nested
    inner class Copy {

        @Test
        fun `Test copy() with component storage update`() {
            val state = EcsState(storageMap = mapOf(Int::class to c0, String::class to c1))

            val newC1 = mockk<ComponentStorage<String>>()
            val copy = state.copy(updatedStorageMap = mapOf(String::class to newC1))

            assertThat(copy.getStorage<Int>()).isSameAs(c0)
            assertThat(copy.getStorage<String>()).all {
                isSameAs(newC1)
                isNotSameAs(c1)
            }
        }

        @Test
        fun `Test copy() with data update`() {
            val data0 = 88
            val data1 = "Test"
            val state = with(EcsBuilder()) {
                addData(data0)
                addData(data1)
                build()
            }

            val newData1 = "New String"
            val copy = state.copy(updatedData = listOf(newData1))

            assertThat(copy.getData<Int>()).isSameAs(data0)
            assertThat(copy.getData<String>()).all {
                isSameAs(newData1)
                isNotSameAs(data1)
            }
        }
    }
}