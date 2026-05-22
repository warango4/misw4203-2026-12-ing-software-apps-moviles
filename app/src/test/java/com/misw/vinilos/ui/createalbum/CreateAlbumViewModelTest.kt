package com.misw.vinilos.ui.createalbum

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.models.AlbumRequest
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
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CreateAlbumViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testDispatchers = TestDispatcherProvider(UnconfinedTestDispatcher())

    @Test
    fun createAlbum_publicaAlbumCuandoElRepositorioRespondeOk() = runTest {
        val expected = Album(1, "Abbey Road", "https://cover.jpg", "Rock")
        val viewModel = CreateAlbumViewModel(
            AlbumRepository(FakeVinilosApiService(albumResult = expected), testDispatchers)
        )

        viewModel.createAlbum("Abbey Road", "https://cover.jpg", "1969-09-26T00:00:00.000Z", "Classic album", "Rock", "Apple Records")
        advanceUntilIdle()

        assertEquals(expected, viewModel.albumCreated.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun createAlbum_publicaErrorCuandoElRepositorioFalla() = runTest {
        val viewModel = CreateAlbumViewModel(
            AlbumRepository(FakeVinilosApiService(error = IllegalStateException("sin conexion")), testDispatchers)
        )

        viewModel.createAlbum("Abbey Road", "https://cover.jpg", "1969-09-26T00:00:00.000Z", "Classic album", "Rock", "Apple Records")
        advanceUntilIdle()

        assertEquals("Ocurrió un error inesperado.", viewModel.error.value)
        assertNull(viewModel.albumCreated.value)
    }

    @Test
    fun createAlbum_isLoadingEsTrueDuranteEjecucionYFalseAlTerminar() = runTest {
        val expected = Album(2, "Thriller", "https://cover.jpg", "Pop")
        val viewModel = CreateAlbumViewModel(
            AlbumRepository(FakeVinilosApiService(albumResult = expected), testDispatchers)
        )

        assertFalse(viewModel.isLoading.value!!)

        viewModel.createAlbum("Thriller", "https://cover.jpg", "1982-11-30T00:00:00.000Z", "Pop album", "Pop", "Epic Records")
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun createAlbum_errorNoPublicaAlbum() = runTest {
        val viewModel = CreateAlbumViewModel(
            AlbumRepository(FakeVinilosApiService(error = RuntimeException("fallo")), testDispatchers)
        )
        viewModel.albumCreated.observeForever {}

        viewModel.createAlbum("Abbey Road", "https://cover.jpg", "1969-09-26T00:00:00.000Z", "Classic album", "Rock", "Apple Records")
        advanceUntilIdle()

        assertNull(viewModel.albumCreated.value)
    }

    @Test
    fun createAlbum_isLoadingEsFalseAlFallar() = runTest {
        val viewModel = CreateAlbumViewModel(
            AlbumRepository(FakeVinilosApiService(error = RuntimeException("fallo")), testDispatchers)
        )

        viewModel.createAlbum("Abbey Road", "https://cover.jpg", "1969-09-26T00:00:00.000Z", "Classic album", "Rock", "Apple Records")
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun createAlbum_publicaAlbumConLosDatosCorrectos() = runTest {
        val expected = Album(7, "Kind of Blue", "https://cover.jpg", "Jazz", "Jazz masterpiece", "Columbia", "1959-08-17T00:00:00.000Z")
        val viewModel = CreateAlbumViewModel(
            AlbumRepository(FakeVinilosApiService(albumResult = expected), testDispatchers)
        )

        viewModel.createAlbum("Kind of Blue", "https://cover.jpg", "1959-08-17T00:00:00.000Z", "Jazz masterpiece", "Jazz", "Columbia")
        advanceUntilIdle()

        assertNotNull(viewModel.albumCreated.value)
        assertEquals("Kind of Blue", viewModel.albumCreated.value?.name)
        assertEquals("Jazz", viewModel.albumCreated.value?.genre)
        assertEquals("Jazz masterpiece", viewModel.albumCreated.value?.description)
        assertEquals("Columbia", viewModel.albumCreated.value?.recordLabel)
        assertEquals(7, viewModel.albumCreated.value?.id)
    }

    private class FakeVinilosApiService(
        private val albumResult: Album? = null,
        private val error: Throwable? = null
    ) : VinilosApiService {
        override suspend fun createAlbum(album: AlbumRequest): Album {
            error?.let { throw it }
            return albumResult ?: throw NotImplementedError()
        }
        override suspend fun getAlbums(): List<Album> = emptyList()
        override suspend fun getAlbum(id: Int, cacheControl: String?): Album = throw NotImplementedError()
        override suspend fun addTrack(albumId: Int, track: TrackRequest): Track = throw NotImplementedError()
        override suspend fun getMusicians() = emptyList<com.misw.vinilos.data.models.Performer>()
        override suspend fun getBands() = emptyList<com.misw.vinilos.data.models.Performer>()
        override suspend fun getMusician(id: Int) = throw NotImplementedError()
        override suspend fun getBand(id: Int) = throw NotImplementedError()
        override suspend fun getCollectors(): List<Collector> = emptyList()
        override suspend fun getCollector(id: Int): Collector = throw NotImplementedError()
    }
}
