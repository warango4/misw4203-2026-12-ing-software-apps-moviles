package com.misw.vinilos.ui.collectors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.models.CollectorAlbum
import com.misw.vinilos.data.models.Performer
import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.data.repository.AlbumRepository
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
class CollectorDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun fetchCollector_invalidId_setsErrorAndDoesNotLoad() = runTest {
        val api = FakeApi(
            collectorById = { throw RuntimeException("should not be called") }
        )

        val vm = CollectorDetailViewModel(
            repository = CollectorRepository(api),
            albumRepository = AlbumRepository(api),
            collectorId = -1
        )
        vm.error.observeForever { }
        vm.isLoading.observeForever { }

        vm.fetchCollector()
        advanceUntilIdle()

        assertEquals("Collector inválido", vm.error.value)
        assertFalse(vm.isLoading.value ?: true)
        assertNull(vm.collector.value)
        assertEquals(emptyList<Album>(), vm.albums.value)
    }

    @Test
    fun fetchCollector_success_setsCollectorAndStopsLoading() = runTest {
        val expected = Collector(id = 1, name = "Ana", telephone = "1", email = "a@a.com")
        val api = FakeApi(collectorById = { expected })

        val vm = CollectorDetailViewModel(CollectorRepository(api), AlbumRepository(api), 1)
        vm.collector.observeForever { }
        vm.isLoading.observeForever { }
        vm.error.observeForever { }

        vm.fetchCollector()
        advanceUntilIdle()

        assertEquals(expected, vm.collector.value)
        assertNull(vm.error.value)
        assertFalse(vm.isLoading.value ?: true)
    }

    @Test
    fun fetchCollector_success_withEmbeddedAlbums_setsAlbumsWithoutFallbackCalls() = runTest {
        val embedded = Album(id = 100, name = "A", cover = "c", genre = "g")
        val expectedCollector = Collector(
            id = 1,
            name = "Ana",
            telephone = "1",
            email = "a@a.com",
            collectorAlbums = listOf(
                CollectorAlbum(albumId = 10, album = embedded)
            )
        )

        var albumCalls = 0
        val api = FakeApi(
            collectorById = { expectedCollector },
            albumById = {
                albumCalls++
                Album(id = it, name = "X", cover = "c", genre = "g")
            }
        )

        val vm = CollectorDetailViewModel(CollectorRepository(api), AlbumRepository(api), 1)
        vm.albums.observeForever { }

        vm.fetchCollector()
        advanceUntilIdle()

        assertEquals(listOf(embedded), vm.albums.value)
        assertEquals(0, albumCalls)
    }

    @Test
    fun fetchCollector_success_withoutEmbeddedAlbums_fetchesDistinctAlbumsById() = runTest {
        val expectedCollector = Collector(
            id = 1,
            name = "Ana",
            telephone = "1",
            email = "a@a.com",
            collectorAlbums = listOf(
                CollectorAlbum(albumId = 100),
                CollectorAlbum(albumId = 100),
                CollectorAlbum(albumId = 200)
            )
        )

        val requested = mutableListOf<Int>()
        val api = FakeApi(
            collectorById = { expectedCollector },
            albumById = {
                requested.add(it)
                Album(id = it, name = "Album $it", cover = "c", genre = "g")
            }
        )

        val vm = CollectorDetailViewModel(CollectorRepository(api), AlbumRepository(api), 1)
        vm.albums.observeForever { }

        vm.fetchCollector()
        advanceUntilIdle()

        assertEquals(listOf(100, 200), requested)
        val albums = vm.albums.value.orEmpty()
        assertEquals(2, albums.size)
        assertEquals(100, albums[0].id)
        assertEquals(200, albums[1].id)
    }

    @Test
    fun fetchCollector_success_withoutCollectorAlbums_setsAlbumsEmpty() = runTest {
        val expectedCollector = Collector(id = 1, name = "Ana", telephone = "1", email = "a@a.com", collectorAlbums = emptyList())
        val api = FakeApi(collectorById = { expectedCollector })

        val vm = CollectorDetailViewModel(CollectorRepository(api), AlbumRepository(api), 1)
        vm.albums.observeForever { }

        vm.fetchCollector()
        advanceUntilIdle()

        assertEquals(emptyList<Album>(), vm.albums.value)
    }

    @Test
    fun fetchCollector_error_setsUserMessageAndStopsLoading() = runTest {
        val api = FakeApi(collectorById = { throw RuntimeException("boom") })

        val vm = CollectorDetailViewModel(CollectorRepository(api), AlbumRepository(api), 1)
        vm.error.observeForever { }
        vm.isLoading.observeForever { }

        vm.fetchCollector()
        advanceUntilIdle()

        assertEquals("No fue posible cargar el detalle del coleccionista", vm.error.value)
        assertFalse(vm.isLoading.value ?: true)
    }

    @Test
    fun fetchCollector_albumFallbackFailure_skipsBrokenAlbumAndKeepsOthers() = runTest {
        val expectedCollector = Collector(
            id = 1,
            name = "Ana",
            telephone = "1",
            email = "a@a.com",
            collectorAlbums = listOf(
                CollectorAlbum(albumId = 100),
                CollectorAlbum(albumId = 200)
            )
        )

        val api = FakeApi(
            collectorById = { expectedCollector },
            albumById = {
                if (it == 200) throw RuntimeException("fail album")
                Album(id = it, name = "Album $it", cover = "c", genre = "g")
            }
        )

        val vm = CollectorDetailViewModel(CollectorRepository(api), AlbumRepository(api), 1)
        vm.albums.observeForever { }

        vm.fetchCollector()
        advanceUntilIdle()

        val albums = vm.albums.value.orEmpty()
        assertEquals(1, albums.size)
        assertEquals(100, albums[0].id)
    }

    @Test
    fun fetchCollector_doesNotStartTwice_whenAlreadyLoading() = runTest {
        var collectorCalls = 0
        val api = FakeApi(
            collectorById = {
                collectorCalls++
                delay(50)
                Collector(id = 1, name = "Ana", telephone = "1", email = "a@a.com")
            }
        )

        val vm = CollectorDetailViewModel(CollectorRepository(api), AlbumRepository(api), 1)
        vm.isLoading.observeForever { }

        vm.fetchCollector()
        runCurrent()
        vm.fetchCollector()
        advanceUntilIdle()

        assertEquals(1, collectorCalls)
    }

    @Test
    fun fetchCollector_success_clearsPreviousErrorOnRetry() = runTest {
        var shouldFail = true
        val api = FakeApi(
            collectorById = {
                if (shouldFail) throw RuntimeException("fail")
                Collector(id = 1, name = "Ana", telephone = "1", email = "a@a.com")
            }
        )

        val vm = CollectorDetailViewModel(CollectorRepository(api), AlbumRepository(api), 1)
        vm.error.observeForever { }

        vm.fetchCollector()
        advanceUntilIdle()
        assertEquals("No fue posible cargar el detalle del coleccionista", vm.error.value)

        shouldFail = false
        vm.fetchCollector()
        advanceUntilIdle()

        assertNull(vm.error.value)
    }

    @Test
    fun fetchCollector_success_embeddedAlbumsPresent_overridesAnyPreviousAlbums() = runTest {
        val embedded = Album(id = 100, name = "A", cover = "c", genre = "g")
        val expectedCollector = Collector(
            id = 1,
            name = "Ana",
            telephone = "1",
            email = "a@a.com",
            collectorAlbums = listOf(CollectorAlbum(albumId = 100, album = embedded))
        )

        val api = FakeApi(collectorById = { expectedCollector })

        val vm = CollectorDetailViewModel(CollectorRepository(api), AlbumRepository(api), 1)
        vm.albums.observeForever { }

        vm.fetchCollector()
        advanceUntilIdle()

        assertEquals(listOf(embedded), vm.albums.value)
        assertTrue(vm.albums.value.orEmpty().isNotEmpty())
    }

    private class FakeApi(
        private val collectorById: suspend (Int) -> Collector,
        private val albumById: suspend (Int) -> Album = { throw NotImplementedError() }
    ) : VinilosApiService {

        override suspend fun getAlbums(): List<Album> = emptyList()

        override suspend fun getAlbum(id: Int): Album = albumById(id)

        override suspend fun getMusicians(): List<Performer> = emptyList()

        override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()

        override suspend fun getBands(): List<Performer> = emptyList()

        override suspend fun getBand(id: Int): Performer = throw NotImplementedError()

        override suspend fun getCollectors(): List<Collector> = emptyList()

        override suspend fun getCollector(id: Int): Collector = collectorById(id)
    }
}

