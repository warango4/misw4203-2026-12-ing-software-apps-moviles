package com.misw.vinilos.data.repository

import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.network.VinilosApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AlbumRepositoryTest {

    @Test
    fun getAlbums_retornaDatosDelApi() = runTest {
        val expected = listOf(Album(1, "A Love Supreme", "cover-url", "Jazz"))
        val api = FakeVinilosApiService(result = expected)
        val repository = AlbumRepository(api)

        val result = repository.getAlbums()

        assertEquals(expected, result)
        assertEquals(1, api.calls)
    }

    @Test
    fun getAlbums_propagaExcepcionDelApi() = runTest {
        val api = FakeVinilosApiService(error = IllegalStateException("fallo api"))
        val repository = AlbumRepository(api)

        try {
            repository.getAlbums()
            org.junit.Assert.fail("Se esperaba IllegalStateException")
        } catch (error: IllegalStateException) {
            assertEquals("fallo api", error.message)
        }
    }

    @Test
    fun getAlbum_retornaDatosDelApi() = runTest {
        val expected = Album(1, "A Love Supreme", "cover-url", "Jazz")
        val api = FakeVinilosApiService(singleResult = expected)
        val repository = AlbumRepository(api)

        val result = repository.getAlbum(1)

        assertEquals(expected, result)
        assertEquals(1, api.calls)
    }

    @Test
    fun getAlbum_propagaExcepcionDelApi() = runTest {
        val api = FakeVinilosApiService(error = IllegalStateException("fallo api detallada"))
        val repository = AlbumRepository(api)

        try {
            repository.getAlbum(1)
            org.junit.Assert.fail("Se esperaba IllegalStateException")
        } catch (error: IllegalStateException) {
            assertEquals("fallo api detallada", error.message)
        }
    }

    private class FakeVinilosApiService(
        private val result: List<Album> = emptyList(),
        private val singleResult: Album? = null,
        private val error: Throwable? = null
    ) : VinilosApiService {

        var calls: Int = 0
            private set

        override suspend fun getAlbums(): List<Album> {
            calls += 1
            error?.let { throw it }
            return result
        }

        override suspend fun getAlbum(id: Int): Album {
            calls += 1
            error?.let { throw it }
            return singleResult ?: throw NotImplementedError()
        }

        override suspend fun getMusicians(): List<com.misw.vinilos.data.models.Performer> = emptyList()
        override suspend fun getBands(): List<com.misw.vinilos.data.models.Performer> = emptyList()
        override suspend fun getMusician(id: Int): com.misw.vinilos.data.models.Performer = throw NotImplementedError()
        override suspend fun getBand(id: Int): com.misw.vinilos.data.models.Performer = throw NotImplementedError()
        override suspend fun getCollectors(): List<com.misw.vinilos.data.models.Collector> = emptyList()
    }
}
