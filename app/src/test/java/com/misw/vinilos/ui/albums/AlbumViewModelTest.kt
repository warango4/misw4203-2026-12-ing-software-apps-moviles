package com.misw.vinilos.ui.albums

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.data.repository.AlbumRepository
import com.misw.vinilos.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AlbumViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun fetchAlbums_publicaAlbumesCuandoElRepositorioRespondeOk() = runTest {
        val expected = listOf(Album(1, "Kind of Blue", "cover-url", "Jazz"))
        val viewModel = AlbumViewModel(
            AlbumRepository(FakeVinilosApiService(result = expected))
        )

        viewModel.fetchAlbums()
        advanceUntilIdle()

        assertEquals(expected, viewModel.albums.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun fetchAlbums_publicaErrorCuandoElRepositorioFalla() = runTest {
        val viewModel = AlbumViewModel(
            AlbumRepository(FakeVinilosApiService(error = IllegalStateException("sin conexion")))
        )

        viewModel.fetchAlbums()
        advanceUntilIdle()

        assertEquals("sin conexion", viewModel.error.value)
    }

    private class FakeVinilosApiService(
        private val result: List<Album> = emptyList(),
        private val error: Throwable? = null
    ) : VinilosApiService {
        override suspend fun getAlbums(): List<Album> {
            error?.let { throw it }
            return result
        }
        override suspend fun getAlbum(id: Int): Album {
            throw NotImplementedError()
        }
        override suspend fun getMusicians() = emptyList<com.misw.vinilos.data.models.Performer>()
        override suspend fun getBands() = emptyList<com.misw.vinilos.data.models.Performer>()
        override suspend fun getMusician(id: Int) = throw NotImplementedError()
        override suspend fun getBand(id: Int) = throw NotImplementedError()
    }
}
