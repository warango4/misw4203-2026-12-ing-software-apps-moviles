package com.misw.vinilos.ui.collectors

import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.data.repository.CollectorRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CollectorRepositoryTest {

    @Test
    fun getCollectors_returnsDataFromApi() = runTest {
        val expected = listOf(
            Collector(1, "Manolo Bellon", "3502457896", "manollo@caracol.com.co"),
            Collector(2, "Jaime Monsalve", "3012357936", "jaime.monsalve@minmusicacol.com.co")
        )
        val api = FakeVinilosApiService(result = expected)
        val repository = CollectorRepository(api)
        val result = repository.getCollectors()
        assertEquals(expected, result)
        assertEquals(2, result.size)
        assertEquals(1, api.calls)
    }

    @Test
    fun getCollectors_whenEmpty_returnsEmptyList() = runTest {
        val api = FakeVinilosApiService(result = emptyList())
        val repository = CollectorRepository(api)
        val result = repository.getCollectors()
        assertEquals(0, result.size)
        assertEquals(1, api.calls)
    }

    @Test
    fun getCollectors_propagatesException() = runTest {
        val expectedError = "Network error"
        val api = FakeVinilosApiService(error = IllegalStateException(expectedError))
        val repository = CollectorRepository(api)
        try {
            repository.getCollectors()
            org.junit.Assert.fail("Expected IllegalStateException")
        } catch (e: Exception) {
            assertEquals(expectedError, e.message)
        }
        assertEquals(1, api.calls)
    }

    private class FakeVinilosApiService(
        private val result: List<Collector> = emptyList(),
        private val error: Throwable? = null
    ) : VinilosApiService {

        var calls = 0
            private set

        override suspend fun getCollectors(): List<Collector> {
            calls++
            error?.let { throw it }
            return result
        }
        override suspend fun getAlbums() = throw NotImplementedError()
        override suspend fun getAlbum(id: Int) = throw NotImplementedError()
        override suspend fun getMusicians() = throw NotImplementedError()
        override suspend fun getBands() = throw NotImplementedError()
        override suspend fun getMusician(id: Int) = throw NotImplementedError()
        override suspend fun getBand(id: Int) = throw NotImplementedError()
    }
}

