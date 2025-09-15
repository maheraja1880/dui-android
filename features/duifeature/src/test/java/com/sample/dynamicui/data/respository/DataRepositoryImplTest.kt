package com.sample.dynamicui.data.respository

import com.sample.dynamicui.data.repository.DataRepositoryImpl
import com.sample.dynamicui.domain.model.AnySerializable
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import app.cash.turbine.awaitItem
import app.cash.turbine.awaitComplete
import app.cash.turbine.test

class DataRepositoryImplTest {

    @Test
    fun `fetchDataForLayout emits usage and selection data`() = runTest {
        val repository = DataRepositoryImpl()
        val layoutId = "testLayout"

        repository.fetchDataForLayout(layoutId).test {
            val first = awaitItem()
            val second = awaitItem()
            awaitComplete()

            // Replace with actual expected values based on AnySerializable implementation
            assertEquals(AnySerializable(repository.usage), first)
            assertEquals(AnySerializable(repository.selection), second)
        }
    }
}