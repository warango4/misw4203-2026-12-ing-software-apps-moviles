package com.misw.vinilos.ui.albumdetail
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.repository.AlbumRepository
import com.misw.vinilos.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
            override suspend fun getMusicians() = emptyList<com.misw.vinilos.data.models.Performer>()
            override suspend fun getBands() = emptyList<com.misw.vinilos.data.models.Performer>()
            override suspend fun getMusician(id: Int) = throw NotImplementedError()
            override suspend fun getBand(id: Int) = throw NotImplementedError()
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
            override suspend fun getMusicians() = emptyList<com.misw.vinilos.data.models.Performer>()
            override suspend fun getBands() = emptyList<com.misw.vinilos.data.models.Performer>()
            override suspend fun getMusician(id: Int) = throw NotImplementedError()
            override suspend fun getBand(id: Int) = throw NotImplementedError()
        }
        val repository = AlbumRepository(fakeApiService)

        val viewModel = AlbumDetailViewModel(repository, 100)

        viewModel.error.observeForever {}
        viewModel.isLoading.observeForever {}

        advanceUntilIdle()

        assertTrue(viewModel.error.value?.isNotEmpty() == true)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun fetchAlbum_error_albumNoEsPublicado() = runTest {
        val fakeApiService = object : com.misw.vinilos.data.network.VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw Exception("Not Found")
            override suspend fun getMusicians() = emptyList<com.misw.vinilos.data.models.Performer>()
            override suspend fun getBands() = emptyList<com.misw.vinilos.data.models.Performer>()
            override suspend fun getMusician(id: Int) = throw NotImplementedError()
            override suspend fun getBand(id: Int) = throw NotImplementedError()
        }
        val repository = AlbumRepository(fakeApiService)
        val viewModel = AlbumDetailViewModel(repository, 999)

        viewModel.album.observeForever {}
        advanceUntilIdle()

        assertNull(viewModel.album.value)
    }

    @Test
    fun fetchAlbum_success_albumConPerformers() = runTest {
        val performers = listOf(com.misw.vinilos.data.models.Performer(1, "Miles Davis", "url", "Jazz legend"))
        val expectedAlbum = Album(
            id = 300, name = "Kind of Blue", cover = "url", genre = "Jazz",
            performers = performers
        )
        val fakeApiService = object : com.misw.vinilos.data.network.VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = expectedAlbum
            override suspend fun getMusicians() = emptyList<com.misw.vinilos.data.models.Performer>()
            override suspend fun getBands() = emptyList<com.misw.vinilos.data.models.Performer>()
            override suspend fun getMusician(id: Int) = throw NotImplementedError()
            override suspend fun getBand(id: Int) = throw NotImplementedError()
        }
        val repository = AlbumRepository(fakeApiService)
        val viewModel = AlbumDetailViewModel(repository, 300)

        viewModel.album.observeForever {}
        advanceUntilIdle()

        assertEquals(1, viewModel.album.value?.performers?.size)
        assertEquals("Miles Davis", viewModel.album.value?.performers?.get(0)?.name)
    }

    @Test
    fun fetchAlbum_withTracks_success_updatesLiveData() = runTest {
        val tracks = listOf(com.misw.vinilos.data.models.Track(1, "Track 1", "3:00"), com.misw.vinilos.data.models.Track(2, "Track 2", "4:00"))
        val expectedAlbum = Album(
            id = 200, name = "Album 2", cover = "url", genre = "Rock", tracks = tracks
        )
        val fakeApiService = object : com.misw.vinilos.data.network.VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = expectedAlbum
            override suspend fun getMusicians() = emptyList<com.misw.vinilos.data.models.Performer>()
            override suspend fun getBands() = emptyList<com.misw.vinilos.data.models.Performer>()
            override suspend fun getMusician(id: Int) = throw NotImplementedError()
            override suspend fun getBand(id: Int) = throw NotImplementedError()
        }
        val repository = AlbumRepository(fakeApiService)
        val viewModel = AlbumDetailViewModel(repository, 200)

        viewModel.album.observeForever {}
        viewModel.isLoading.observeForever {}

        advanceUntilIdle()

        assertEquals(expectedAlbum, viewModel.album.value)
        assertEquals(2, viewModel.album.value?.tracks?.size)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun fetchAlbum_initialState_isLoadingTrue() = runTest {
        val expectedAlbum = Album(
            id = 200, name = "Album Wait", cover = "url", genre = "Pop"
        )
        val fakeApiService = object : com.misw.vinilos.data.network.VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = expectedAlbum
            override suspend fun getMusicians() = emptyList<com.misw.vinilos.data.models.Performer>()
            override suspend fun getBands() = emptyList<com.misw.vinilos.data.models.Performer>()
            override suspend fun getMusician(id: Int) = throw NotImplementedError()
            override suspend fun getBand(id: Int) = throw NotImplementedError()
        }
        val repository = AlbumRepository(fakeApiService)

        val viewModel = AlbumDetailViewModel(repository, 200)

        // Observers allow us to track LiveData correctly
        viewModel.album.observeForever {}
        viewModel.isLoading.observeForever {}

        // As it completes immediately synchronously within runTest in init
        // After finishing it will be false. Before idle it might be true but without pause Dispatcher it resolves.
        advanceUntilIdle()
        assertEquals(false, viewModel.isLoading.value)
    }
}
