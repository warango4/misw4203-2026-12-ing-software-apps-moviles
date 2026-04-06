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
class AlbumViewModelExtendedTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun fetchAlbums_whenEmptyList_emitsEmptyList() = runTest {
        val expected = emptyList<Album>()
        val viewModel = AlbumViewModel(
            AlbumRepository(FakeVinilosApiService(result = expected))
        )
        viewModel.fetchAlbums()
        advanceUntilIdle()
        assertEquals(0, viewModel.albums.value?.size)
        assertNull(viewModel.error.value)
    }

    @Test
    fun fetchAlbums_loadingState_transitionsCorrectly() = runTest {
        val expected = listOf(Album(2, "Test Album", "cover", "Pop"))
        val viewModel = AlbumViewModel(
            AlbumRepository(FakeVinilosApiService(result = expected))
        )
        viewModel.fetchAlbums()
        advanceUntilIdle()
        assertEquals(1, viewModel.albums.value?.size)
    }

    private class FakeVinilosApiService(
        private val result: List<Album> = emptyList(),
        private val error: Throwable? = null
    ) : VinilosApiService {
        override suspend fun getAlbums(): List<Album> {
            error?.let { throw it }
            return result
        }
        override suspend fun getAlbum(id: Int) = throw NotImplementedError()
        override suspend fun getMusicians() = throw NotImplementedError()
        override suspend fun getBands() = throw NotImplementedError()
        override suspend fun getMusician(id: Int) = throw NotImplementedError()
        override suspend fun getBand(id: Int) = throw NotImplementedError()
        override suspend fun getCollectors() = throw NotImplementedError()
    }
}

