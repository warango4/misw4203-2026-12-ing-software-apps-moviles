package com.misw.vinilos.ui.performers
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.misw.vinilos.data.models.Performer
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.data.repository.PerformerRepository
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
class PerformerViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    @Test
    fun fetchPerformers_success_updatesLiveData() = runTest {
        val expectedPerformer = Performer(
            id = 1,
            name = "Aventura",
            image = "url",
            description = "Bachata group"
        )
        val fakeApiService = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = listOf(expectedPerformer)
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
        }
        val repository = PerformerRepository(fakeApiService)
        val viewModel = PerformerViewModel(repository)
        viewModel.performers.observeForever {}
        viewModel.isLoading.observeForever {}
        viewModel.fetchPerformers()
        advanceUntilIdle()
        assertEquals(1, viewModel.performers.value?.size)
        assertEquals("Aventura", viewModel.performers.value?.get(0)?.name)
        assertEquals(false, viewModel.isLoading.value)
    }
    @Test
    fun fetchPerformers_error_updatesErrorLiveData() = runTest {
        val fakeApiService = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = throw Exception("API Error")
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
        }
        val repository = PerformerRepository(fakeApiService)

        val viewModel = PerformerViewModel(repository)
        viewModel.error.observeForever {}
        viewModel.isLoading.observeForever {}

        viewModel.fetchPerformers()
        advanceUntilIdle()

        assertTrue(viewModel.error.value?.isNotEmpty() == true)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun fetchPerformers_combinaMusiciansYBands() = runTest {
        val musician = Performer(id = 1, name = "Carlos Vives", image = "url", description = "Vallenato")
        val band = Performer(id = 2, name = "Aterciopelados", image = "url", description = "Rock")
        val fakeApiService = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = listOf(musician)
            override suspend fun getBands(): List<Performer> = listOf(band)
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
        }
        val repository = PerformerRepository(fakeApiService)
        val viewModel = PerformerViewModel(repository)
        viewModel.performers.observeForever {}

        viewModel.fetchPerformers()
        advanceUntilIdle()

        assertEquals(2, viewModel.performers.value?.size)
        assertEquals("Aterciopelados", viewModel.performers.value?.get(0)?.name)
        assertEquals("Carlos Vives", viewModel.performers.value?.get(1)?.name)
    }

    @Test
    fun fetchPerformers_emptyResponse_updatesLiveDataWithEmptyList() = runTest {
        val fakeApiService = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
        }
        val repository = PerformerRepository(fakeApiService)

        val viewModel = PerformerViewModel(repository)
        viewModel.performers.observeForever {}
        viewModel.isLoading.observeForever {}

        viewModel.fetchPerformers()
        advanceUntilIdle()

        assertEquals(0, viewModel.performers.value?.size)
        assertEquals(false, viewModel.isLoading.value)
    }
}
