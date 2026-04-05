package com.misw.vinilos.ui.albumdetail
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.repository.AlbumRepository
import com.misw.vinilos.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AlbumDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun fetchAlbum_success_updatesLiveData() = runTest {
        val expectedAlbum = Album(
            id = 100,
            name = "Poeta Halley",
            cover = "cover_url",
            genre = "Salsa",
            description = "Description",
            recordLabel = "Sony",
            releaseDate = "2020-01-01",
            tracks = emptyList()
        )

        val fakeApiService = object : com.misw.vinilos.data.network.VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = expectedAlbum
        }
        val repository = AlbumRepository(fakeApiService)

        val viewModel = AlbumDetailViewModel(repository, 100)

        viewModel.album.observeForever {}
        viewModel.isLoading.observeForever {}

        advanceUntilIdle()

        assertEquals(expectedAlbum, viewModel.album.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun fetchAlbum_error_updatesErrorLiveData() = runTest {
        val fakeApiService = object : com.misw.vinilos.data.network.VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw Exception("Network Error")
        }
        val repository = AlbumRepository(fakeApiService)

        val viewModel = AlbumDetailViewModel(repository, 100)

        viewModel.error.observeForever {}
        viewModel.isLoading.observeForever {}

        advanceUntilIdle()

        assertTrue(viewModel.error.value?.isNotEmpty() == true)
        assertEquals(false, viewModel.isLoading.value)
    }
}
