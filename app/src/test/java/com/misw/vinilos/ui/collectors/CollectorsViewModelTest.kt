package com.misw.vinilos.ui.collectors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.models.Performer
import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.data.repository.CollectorRepository
import com.misw.vinilos.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
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
class CollectorsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun fetchCollectors_success_updatesCollectorsSortedAndStopsLoading() = runTest {
        val c1 = Collector(id = 1, name = "Zoe", telephone = "1", email = "z@a.com")
        val c2 = Collector(id = 2, name = "Ana", telephone = "2", email = "a@a.com")

        val api = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> = listOf(c1, c2)
        }

        val vm = CollectorsViewModel(CollectorRepository(api))
        vm.collectors.observeForever { }
        vm.isLoading.observeForever { }
        vm.error.observeForever { }

        vm.fetchCollectors()
        advanceUntilIdle()

        assertEquals(listOf(c2, c1), vm.collectors.value)
        assertFalse(vm.isLoading.value ?: true)
        assertNull(vm.error.value)
    }

    @Test
    fun fetchCollectors_emptyResponse_setsEmptyListAndStopsLoading() = runTest {
        val api = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> = emptyList()
        }

        val vm = CollectorsViewModel(CollectorRepository(api))
        vm.collectors.observeForever { }
        vm.isLoading.observeForever { }

        vm.fetchCollectors()
        advanceUntilIdle()

        assertEquals(emptyList<Collector>(), vm.collectors.value)
        assertFalse(vm.isLoading.value ?: true)
    }

    @Test
    fun fetchCollectors_error_setsUserFriendlyErrorAndStopsLoading() = runTest {
        val api = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> = throw Exception("Boom")
        }

        val vm = CollectorsViewModel(CollectorRepository(api))
        vm.error.observeForever { }
        vm.isLoading.observeForever { }

        vm.fetchCollectors()
        advanceUntilIdle()

        assertEquals("No fue posible cargar coleccionistas", vm.error.value)
        assertFalse(vm.isLoading.value ?: true)
    }

    @Test
    fun fetchCollectors_clearsPreviousErrorOnRetry() = runTest {
        var shouldFail = true
        val api = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> {
                if (shouldFail) throw Exception("fail")
                return emptyList()
            }
        }

        val vm = CollectorsViewModel(CollectorRepository(api))
        vm.error.observeForever { }

        vm.fetchCollectors()
        advanceUntilIdle()
        assertEquals("No fue posible cargar coleccionistas", vm.error.value)

        shouldFail = false
        vm.fetchCollectors()
        advanceUntilIdle()

        assertNull(vm.error.value)
    }

    @Test
    fun fetchCollectors_doesNotTriggerAgainWhenAlreadyLoading() = runTest {
        var calls = 0
        val api = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> {
                calls++
                // Keep the first request in-flight so _isLoading remains true
                delay(50)
                return emptyList()
            }
        }

        val vm = CollectorsViewModel(CollectorRepository(api))
        vm.isLoading.observeForever { }

        // Force loading true before calling
        // (guard clause should return before launching any coroutine)
        vm.fetchCollectors()
        runCurrent()
        // after first call, it will set loading true synchronously; second call should be ignored
        vm.fetchCollectors()
        advanceUntilIdle()

        assertEquals(1, calls)
    }

    @Test
    fun fetchCollectors_success_doesNotSetError() = runTest {
        val api = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> = emptyList()
        }

        val vm = CollectorsViewModel(CollectorRepository(api))
        vm.error.observeForever { }

        vm.fetchCollectors()
        advanceUntilIdle()

        assertNull(vm.error.value)
    }

    @Test
    fun fetchCollectors_error_doesNotOverwriteExistingCollectors() = runTest {
        val initial = listOf(Collector(id = 1, name = "Ana", telephone = "1", email = "a@a.com"))
        var shouldFail = false

        val api = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> {
                if (shouldFail) throw Exception("fail")
                return initial
            }
        }

        val vm = CollectorsViewModel(CollectorRepository(api))
        vm.collectors.observeForever { }

        vm.fetchCollectors()
        advanceUntilIdle()
        assertEquals(initial, vm.collectors.value)

        shouldFail = true
        vm.fetchCollectors()
        advanceUntilIdle()

        assertEquals(initial, vm.collectors.value)
    }

    @Test
    fun fetchCollectors_setsLoadingTrueImmediately() = runTest {
        val api = object : VinilosApiService {
            override suspend fun getAlbums(): List<Album> = emptyList()
            override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
            override suspend fun getMusicians(): List<Performer> = emptyList()
            override suspend fun getBands(): List<Performer> = emptyList()
            override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
            override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
            override suspend fun getCollectors(): List<Collector> = emptyList()
        }

        val vm = CollectorsViewModel(CollectorRepository(api))
        var sawLoadingTrue = false
        vm.isLoading.observeForever { if (it == true) sawLoadingTrue = true }

        vm.fetchCollectors()
        advanceUntilIdle()

        assertTrue(sawLoadingTrue)
    }
}

