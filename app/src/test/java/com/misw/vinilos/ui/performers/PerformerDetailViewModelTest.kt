package com.misw.vinilos.ui.performers
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.models.Performer
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.data.repository.PerformerRepository
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
class PerformerDetailViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    @Test
    fun fetchPerformerDetail_band_success_updatesLiveData() = runTest {
        val expectedBand = Performer(id = 10, name = "Queen", image = "url", description = "Rock")
        val fakeApiService = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer {
                if (id == 10) return expectedBand else throw Exception("Not found")
            }
            override suspend fun getCollectors(): List<Collector> = emptyList()
        }
        val repository = PerformerRepository(fakeApiService)
        val viewModel = PerformerDetailViewModel(repository, performerId = 10, isBand = true)
        viewModel.performer.observeForever {}
        viewModel.isLoading.observeForever {}
        advanceUntilIdle()
        assertEquals(expectedBand, viewModel.performer.value)
        assertEquals(false, viewModel.isLoading.value)
    }
    @Test
    fun fetchPerformerDetail_musician_error_updatesErrorLiveData() = runTest {
        val fakeApiService = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw Exception("API Musician Error")
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> = emptyList()
        }
        val repository = PerformerRepository(fakeApiService)
        val viewModel = PerformerDetailViewModel(repository, performerId = 15, isBand = false)
        viewModel.error.observeForever {}
        viewModel.isLoading.observeForever {}
        advanceUntilIdle()
        assertTrue(viewModel.error.value?.isNotEmpty() == true)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun fetchPerformerDetail_musician_success_updatesLiveData() = runTest {
        val expectedMusician = Performer(id = 15, name = "Ruben Blades", image = "url", description = "Salsa")
        val fakeApiService = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = expectedMusician
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> = emptyList()
        }
        val repository = PerformerRepository(fakeApiService)
        val viewModel = PerformerDetailViewModel(repository, performerId = 15, isBand = false)
        viewModel.performer.observeForever {}
        viewModel.isLoading.observeForever {}
        advanceUntilIdle()
        assertEquals(expectedMusician, viewModel.performer.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun fetchPerformerDetail_band_success_performerNoEsNull() = runTest {
        val expectedBand = Performer(id = 5, name = "Soda Stereo", image = "url", description = "Rock")
        val fakeApiService = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = expectedBand
            override suspend fun getCollectors(): List<Collector> = emptyList()
        }
        val repository = PerformerRepository(fakeApiService)
        val viewModel = PerformerDetailViewModel(repository, performerId = 5, isBand = true)
        viewModel.performer.observeForever {}
        advanceUntilIdle()

        assert(viewModel.performer.value != null)
        assertNull(viewModel.error.value)
    }

    @Test
    fun fetchPerformerDetail_band_error_updatesErrorLiveData() = runTest {
        val fakeApiService = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw Exception("API Band Error")
            override suspend fun getCollectors(): List<Collector> = emptyList()
        }
        val repository = PerformerRepository(fakeApiService)
        val viewModel = PerformerDetailViewModel(repository, performerId = 20, isBand = true)
        viewModel.error.observeForever {}
        viewModel.isLoading.observeForever {}
        advanceUntilIdle()
        assertTrue(viewModel.error.value?.isNotEmpty() == true)
        assertEquals(false, viewModel.isLoading.value)
    }
}
