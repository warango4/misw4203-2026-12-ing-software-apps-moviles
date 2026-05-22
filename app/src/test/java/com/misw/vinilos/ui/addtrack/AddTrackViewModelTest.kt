package com.misw.vinilos.ui.addtrack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.models.Track
import com.misw.vinilos.data.models.TrackRequest
import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.data.repository.AlbumRepository
import com.misw.vinilos.testutils.MainDispatcherRule
import com.misw.vinilos.testutils.TestDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AddTrackViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testDispatchers = TestDispatcherProvider(UnconfinedTestDispatcher())

    @Test
    fun addTrack_publicaTrackCuandoElRepositorioRespondeOk() = runTest {
        val expected = Track(1, "Money", "6:22")
        val viewModel = AddTrackViewModel(
            AlbumRepository(FakeVinilosApiService(trackResult = expected), testDispatchers)
        )

        viewModel.addTrack(5, "Money", "6:22")
        advanceUntilIdle()

        assertEquals(expected, viewModel.trackAdded.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun addTrack_publicaErrorCuandoElRepositorioFalla() = runTest {
        val viewModel = AddTrackViewModel(
            AlbumRepository(FakeVinilosApiService(error = IllegalStateException("sin conexion")), testDispatchers)
        )

        viewModel.addTrack(5, "Money", "6:22")
        advanceUntilIdle()

        assertEquals("Ocurrió un error inesperado.", viewModel.error.value)
        assertNull(viewModel.trackAdded.value)
    }

    @Test
    fun addTrack_isLoadingEsTrueduranteEjecucionYFalseAlTerminar() = runTest {
        val expected = Track(1, "Time", "7:05")
        val viewModel = AddTrackViewModel(
            AlbumRepository(FakeVinilosApiService(trackResult = expected), testDispatchers)
        )

        assertFalse(viewModel.isLoading.value!!)

        viewModel.addTrack(5, "Time", "7:05")
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun addTrack_errorNoPublicaTrack() = runTest {
        val viewModel = AddTrackViewModel(
            AlbumRepository(FakeVinilosApiService(error = RuntimeException("fallo")), testDispatchers)
        )
        viewModel.trackAdded.observeForever {}

        viewModel.addTrack(5, "Money", "6:22")
        advanceUntilIdle()

        assertNull(viewModel.trackAdded.value)
    }

    @Test
    fun addTrack_isLoadingEsFalseAlFallar() = runTest {
        val viewModel = AddTrackViewModel(
            AlbumRepository(FakeVinilosApiService(error = RuntimeException("fallo")), testDispatchers)
        )

        viewModel.addTrack(5, "Money", "6:22")
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun addTrack_publicaTrackConLosDatosCorrectos() = runTest {
        val expected = Track(7, "Brain Damage", "3:50")
        val viewModel = AddTrackViewModel(
            AlbumRepository(FakeVinilosApiService(trackResult = expected), testDispatchers)
        )

        viewModel.addTrack(3, "Brain Damage", "3:50")
        advanceUntilIdle()

        assertEquals("Brain Damage", viewModel.trackAdded.value?.name)
        assertEquals("3:50", viewModel.trackAdded.value?.duration)
        assertEquals(7, viewModel.trackAdded.value?.id)
    }

    private class FakeVinilosApiService(
        private val trackResult: Track? = null,
        private val error: Throwable? = null
    ) : VinilosApiService {
        override suspend fun addTrack(albumId: Int, track: TrackRequest): Track {
            error?.let { throw it }
            return trackResult ?: throw NotImplementedError()
        }
        override suspend fun getAlbums(): List<Album> = emptyList()
        override suspend fun getAlbum(id: Int, cacheControl: String?): Album = throw NotImplementedError()
        override suspend fun createAlbum(album: com.misw.vinilos.data.models.AlbumRequest): Album = throw NotImplementedError()
        override suspend fun getMusicians() = emptyList<com.misw.vinilos.data.models.Performer>()
        override suspend fun getBands() = emptyList<com.misw.vinilos.data.models.Performer>()
        override suspend fun getMusician(id: Int) = throw NotImplementedError()
        override suspend fun getBand(id: Int) = throw NotImplementedError()
        override suspend fun getCollectors(): List<Collector> = emptyList()
        override suspend fun getCollector(id: Int): Collector = throw NotImplementedError()
    }
}
