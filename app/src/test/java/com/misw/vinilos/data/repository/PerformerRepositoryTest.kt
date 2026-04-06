package com.misw.vinilos.data.repository
import com.misw.vinilos.data.models.Performer
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.network.VinilosApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PerformerRepositoryTest {
    @Test
    fun getPerformers_combinesMusiciansAndBandsAndSortsByName() = runTest {
        val musicians = listOf(Performer(1, "Zendaya", "url1", "desc1"))
        val bands = listOf(Performer(2, "Aventura", "url2", "desc2"))
        val api = FakePerformerApiService(musiciansResult = musicians, bandsResult = bands)
        val repository = PerformerRepository(api)
        val result = repository.getPerformers()
        assertEquals(2, result.size)
        assertEquals("Aventura", result[0].name)
        assertEquals("Zendaya", result[1].name)
    }
    @Test
    fun getPerformers_bothEmpty_returnsEmpty() = runTest {
        val api = FakePerformerApiService()
        val repository = PerformerRepository(api)
        val result = repository.getPerformers()
        assertEquals(0, result.size)
    }
    @Test
    fun getPerformers_musiciansApiFails_throwsException() = runTest {
        val api = FakePerformerApiService(musiciansError = RuntimeException("Error API Musicians"))
        val repository = PerformerRepository(api)
        try {
            repository.getPerformers()
            org.junit.Assert.fail("Expected exception")
        } catch (e: Exception) {
            assertEquals("Error API Musicians", e.message)
        }
    }
    @Test
    fun getPerformers_bandsApiFails_throwsException() = runTest {
        val api = FakePerformerApiService(bandsError = RuntimeException("Error API Bands"))
        val repository = PerformerRepository(api)
        try {
            repository.getPerformers()
            org.junit.Assert.fail("Expected exception")
        } catch (e: Exception) {
            assertEquals("Error API Bands", e.message)
        }
    }
    @Test
    fun getPerformers_onlyMusicians_returnsSortedMusicians() = runTest {
        val musicians = listOf(
            Performer(2, "Zendaya", "url", "desc"),
            Performer(1, "Bruno Mars", "url", "desc")
        )
        val api = FakePerformerApiService(musiciansResult = musicians)
        val repository = PerformerRepository(api)
        val result = repository.getPerformers()
        assertEquals(2, result.size)
        assertEquals("Bruno Mars", result[0].name)
        assertEquals("Zendaya", result[1].name)
    }
    private class FakePerformerApiService(
        private val musiciansResult: List<Performer> = emptyList(),
        private val bandsResult: List<Performer> = emptyList(),
        private val musiciansError: Throwable? = null,
        private val bandsError: Throwable? = null
    ) : VinilosApiService {
        override suspend fun getAlbums(): List<Album> = emptyList()
        override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
        override suspend fun getMusicians(): List<Performer> {
            musiciansError?.let { throw it }
            return musiciansResult
        }
        override suspend fun getBands(): List<Performer> {
            bandsError?.let { throw it }
            return bandsResult
        }
        override suspend fun getMusician(id: Int): Performer = throw NotImplementedError()
        override suspend fun getBand(id: Int): Performer = throw NotImplementedError()
        override suspend fun getCollectors(): List<com.misw.vinilos.data.models.Collector> = emptyList()
    }
}
