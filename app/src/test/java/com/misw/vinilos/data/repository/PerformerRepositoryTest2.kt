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
class PerformerRepositoryTest2 {
    @Test
    fun getPerformer_isBand_callsBandApi() = runTest {
        val expectedBand = Performer(id = 1, name = "Band", image = "url", description = "desc")
        val api = FakePerformerDetailApiService(bandResult = expectedBand)
        val repository = PerformerRepository(api)
        val result = repository.getPerformer(1, true)
        assertEquals("Band", result.name)
    }
    @Test
    fun getPerformer_isMusician_callsMusicianApi() = runTest {
        val expectedMusician = Performer(id = 2, name = "Musician", image = "url", description = "desc")
        val api = FakePerformerDetailApiService(musicianResult = expectedMusician)
        val repository = PerformerRepository(api)
        val result = repository.getPerformer(2, false)
        assertEquals("Musician", result.name)
    }
    @Test
    fun getPerformer_apiFails_throwsException() = runTest {
        val api = FakePerformerDetailApiService(error = Exception("Detail Error"))
        val repository = PerformerRepository(api)
        try {
            repository.getPerformer(1, true)
            org.junit.Assert.fail("Expected exception")
        } catch (e: Exception) {
            assertEquals("Detail Error", e.message)
        }
    }
    private class FakePerformerDetailApiService(
        private val musicianResult: Performer? = null,
        private val bandResult: Performer? = null,
        private val error: Exception? = null
    ) : VinilosApiService {
        override suspend fun getAlbums(): List<Album> = emptyList()
        override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
        override suspend fun getMusicians(): List<Performer> = emptyList()
        override suspend fun getBands(): List<Performer> = emptyList()
        override suspend fun getMusician(id: Int): Performer {
            error?.let { throw it }
            return musicianResult ?: throw NotImplementedError()
        }
        override suspend fun getCollectors(): List<com.misw.vinilos.data.models.Collector> = emptyList()
        override suspend fun getBand(id: Int): Performer {
            error?.let { throw it }
            return bandResult ?: throw NotImplementedError()
        }
    }
}
