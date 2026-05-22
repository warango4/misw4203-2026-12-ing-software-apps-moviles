package com.misw.vinilos.data.repository

import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.models.AlbumRequest
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.models.Track
import com.misw.vinilos.data.models.TrackRequest
import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.testutils.TestDispatcherProvider
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CreateAlbumRepositoryTest {

    private val testDispatchers = TestDispatcherProvider(UnconfinedTestDispatcher())

    @Test
    fun createAlbum_retornaDatosDelApi() = runTest {
        val expected = Album(1, "Abbey Road", "https://cover.jpg", "Rock")
        val api = FakeVinilosApiService(albumResult = expected)
        val repository = AlbumRepository(api, testDispatchers)

        val result = repository.createAlbum(AlbumRequest("Abbey Road", "https://cover.jpg", "1969-09-26T00:00:00.000Z", "Classic album", "Rock", "Apple Records"))

        assertEquals(expected, result)
        assertEquals(1, api.calls)
    }

    @Test
    fun createAlbum_propagaExcepcionDelApi() = runTest {
        val api = FakeVinilosApiService(error = IllegalStateException("fallo api"))
        val repository = AlbumRepository(api, testDispatchers)

        try {
            repository.createAlbum(AlbumRequest("Abbey Road", "https://cover.jpg", "1969-09-26T00:00:00.000Z", "Classic album", "Rock", "Apple Records"))
            org.junit.Assert.fail("Se esperaba IllegalStateException")
        } catch (error: IllegalStateException) {
            assertEquals("fallo api", error.message)
        }
    }

    @Test
    fun createAlbum_enviaLosParametrosCorrectos() = runTest {
        val expected = Album(2, "Thriller", "https://cover.jpg", "Pop")
        val api = FakeVinilosApiService(albumResult = expected)
        val repository = AlbumRepository(api, testDispatchers)

        repository.createAlbum(AlbumRequest("Thriller", "https://cover.jpg", "1982-11-30T00:00:00.000Z", "Pop masterpiece", "Pop", "Epic Records"))

        assertEquals("Thriller", api.lastRequest?.name)
        assertEquals("https://cover.jpg", api.lastRequest?.cover)
        assertEquals("1982-11-30T00:00:00.000Z", api.lastRequest?.releaseDate)
        assertEquals("Pop masterpiece", api.lastRequest?.description)
        assertEquals("Pop", api.lastRequest?.genre)
        assertEquals("Epic Records", api.lastRequest?.recordLabel)
    }

    @Test
    fun createAlbum_retornaElAlbumConIdAsignadoPorElServidor() = runTest {
        val expected = Album(42, "Kind of Blue", "https://cover.jpg", "Jazz")
        val api = FakeVinilosApiService(albumResult = expected)
        val repository = AlbumRepository(api, testDispatchers)

        val result = repository.createAlbum(AlbumRequest("Kind of Blue", "https://cover.jpg", "1959-08-17T00:00:00.000Z", "Jazz masterpiece", "Jazz", "Columbia"))

        assertEquals(42, result.id)
    }

    private class FakeVinilosApiService(
        private val albumResult: Album? = null,
        private val error: Throwable? = null
    ) : VinilosApiService {

        var calls: Int = 0
            private set
        var lastRequest: AlbumRequest? = null
            private set

        override suspend fun createAlbum(album: AlbumRequest): Album {
            calls += 1
            lastRequest = album
            error?.let { throw it }
            return albumResult ?: throw NotImplementedError()
        }

        override suspend fun getAlbums(): List<Album> = emptyList()
        override suspend fun getAlbum(id: Int, cacheControl: String?): Album = throw NotImplementedError()
        override suspend fun addTrack(albumId: Int, track: TrackRequest): Track = throw NotImplementedError()
        override suspend fun getMusicians(): List<com.misw.vinilos.data.models.Performer> = emptyList()
        override suspend fun getBands(): List<com.misw.vinilos.data.models.Performer> = emptyList()
        override suspend fun getMusician(id: Int): com.misw.vinilos.data.models.Performer = throw NotImplementedError()
        override suspend fun getBand(id: Int): com.misw.vinilos.data.models.Performer = throw NotImplementedError()
        override suspend fun getCollectors(): List<Collector> = emptyList()
        override suspend fun getCollector(id: Int): Collector = throw NotImplementedError()
    }
}
