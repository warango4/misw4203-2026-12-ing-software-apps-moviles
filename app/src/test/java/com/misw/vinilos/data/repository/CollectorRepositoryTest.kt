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
class CollectorRepositoryTest {

    @Test
    fun getCollectors_success_returnsListFromApi() = runTest {
        val expected = listOf(
            Collector(id = 1, name = "Ana", telephone = "1", email = "a@a.com"),
            Collector(id = 2, name = "Beto", telephone = "2", email = "b@b.com")
        )

        val api = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> = expected
            override suspend fun getCollector(id: Int): Collector = throw NotImplementedError()
        }

        val repo = CollectorRepository(api)
        val result = repo.getCollectors()

        assertEquals(2, result.size)
        assertEquals("Ana", result[0].name)
    }

    @Test
    fun getCollectors_success_returnsSameInstanceToAvoidUnneededCopies() = runTest {
        val expected = emptyList<Collector>()

        val api = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> = expected
            override suspend fun getCollector(id: Int): Collector = throw NotImplementedError()
        }

        val repo = CollectorRepository(api)
        val result = repo.getCollectors()

        assertSame(expected, result)
    }

    @Test(expected = RuntimeException::class)
    fun getCollectors_error_rethrowsException() = runTest {
        val api = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> = throw RuntimeException("API Error")
            override suspend fun getCollector(id: Int): Collector = throw NotImplementedError()
        }

        val repo = CollectorRepository(api)
        repo.getCollectors()
    }
}

