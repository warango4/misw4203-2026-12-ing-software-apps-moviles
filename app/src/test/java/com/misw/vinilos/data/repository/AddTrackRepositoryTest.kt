package com.misw.vinilos.data.repository

import com.misw.vinilos.data.models.Album
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
class AddTrackRepositoryTest {

    private val testDispatchers = TestDispatcherProvider(UnconfinedTestDispatcher())

    @Test
    fun addTrack_retornaDatosDelApi() = runTest {
        val expected = Track(1, "Money", "6:22")
        val api = FakeVinilosApiService(trackResult = expected)
        val repository = AlbumRepository(api, testDispatchers)

        val result = repository.addTrack(5, TrackRequest("Money", "6:22"))

        assertEquals(expected, result)
        assertEquals(1, api.calls)
    }

    @Test
    fun addTrack_propagaExcepcionDelApi() = runTest {
        val api = FakeVinilosApiService(error = IllegalStateException("fallo api"))
        val repository = AlbumRepository(api, testDispatchers)

        try {
            repository.addTrack(5, TrackRequest("Money", "6:22"))
            org.junit.Assert.fail("Se esperaba IllegalStateException")
        } catch (error: IllegalStateException) {
            assertEquals("fallo api", error.message)
        }
    }

    @Test
    fun addTrack_enviaElAlbumIdCorrecto() = runTest {
        val expected = Track(2, "Time", "7:05")
        val api = FakeVinilosApiService(trackResult = expected)
        val repository = AlbumRepository(api, testDispatchers)

        repository.addTrack(42, TrackRequest("Time", "7:05"))

        assertEquals(42, api.lastAlbumId)
    }

    @Test
    fun addTrack_enviaLosParametrosCorrectos() = runTest {
        val expected = Track(3, "Brain Damage", "3:50")
        val api = FakeVinilosApiService(trackResult = expected)
        val repository = AlbumRepository(api, testDispatchers)

        repository.addTrack(1, TrackRequest("Brain Damage", "3:50"))

        assertEquals("Brain Damage", api.lastRequest?.name)
        assertEquals("3:50", api.lastRequest?.duration)
    }

    private class FakeVinilosApiService(
        private val trackResult: Track? = null,
        private val error: Throwable? = null
    ) : VinilosApiService {

        var calls: Int = 0
            private set
        var lastAlbumId: Int? = null
            private set
        var lastRequest: TrackRequest? = null
            private set

        override suspend fun addTrack(albumId: Int, track: TrackRequest): Track {
            calls += 1
            lastAlbumId = albumId
            lastRequest = track
            error?.let { throw it }
            return trackResult ?: throw NotImplementedError()
        }

        override suspend fun getAlbums(): List<Album> = emptyList()
        override suspend fun getAlbum(id: Int, cacheControl: String?): Album = throw NotImplementedError()
        override suspend fun createAlbum(album: com.misw.vinilos.data.models.AlbumRequest): Album = throw NotImplementedError()
        override suspend fun getMusicians(): List<com.misw.vinilos.data.models.Performer> = emptyList()
        override suspend fun getBands(): List<com.misw.vinilos.data.models.Performer> = emptyList()
        override suspend fun getMusician(id: Int): com.misw.vinilos.data.models.Performer = throw NotImplementedError()
        override suspend fun getBand(id: Int): com.misw.vinilos.data.models.Performer = throw NotImplementedError()
        override suspend fun getCollectors(): List<Collector> = emptyList()
        override suspend fun getCollector(id: Int): Collector = throw NotImplementedError()
    }
}
