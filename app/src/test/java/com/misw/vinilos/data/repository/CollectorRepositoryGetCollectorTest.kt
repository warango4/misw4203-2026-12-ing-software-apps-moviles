package com.misw.vinilos.data.repository

import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.models.Performer
import com.misw.vinilos.data.network.VinilosApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CollectorRepositoryGetCollectorTest {

    @Test
    fun getCollector_success_returnsCollectorFromApi() = runTest {
        val expected = Collector(id = 7, name = "Carlos", telephone = "123", email = "c@a.com")

        val api = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> = emptyList()
            override suspend fun getCollector(id: Int): Collector {
                assertEquals(7, id)
                return expected
            }
        }

        val repo = CollectorRepository(api)
        val result = repo.getCollector(7)

        assertEquals(7, result.id)
        assertEquals("Carlos", result.name)
    }

    @Test
    fun getCollector_success_returnsSameInstanceToAvoidUnneededCopies() = runTest {
        val expected = Collector(id = 1, name = "Ana", telephone = "1", email = "a@a.com")

        val api = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> = emptyList()
            override suspend fun getCollector(id: Int): Collector = expected
        }

        val repo = CollectorRepository(api)
        val result = repo.getCollector(99)

        assertSame(expected, result)
    }

    @Test(expected = RuntimeException::class)
    fun getCollector_error_rethrowsException() = runTest {
        val api = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> = emptyList()
            override suspend fun getCollector(id: Int): Collector = throw RuntimeException("API Error")
        }

        val repo = CollectorRepository(api)
        repo.getCollector(1)
    }
}

