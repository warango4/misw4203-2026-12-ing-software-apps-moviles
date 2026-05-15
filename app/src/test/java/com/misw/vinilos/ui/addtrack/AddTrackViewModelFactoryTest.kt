package com.misw.vinilos.ui.addtrack

import androidx.lifecycle.ViewModel
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.models.Track
import com.misw.vinilos.data.models.TrackRequest
import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.data.repository.AlbumRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AddTrackViewModelFactoryTest {

    private val repository = AlbumRepository(object : VinilosApiService {
        override suspend fun getAlbums(): List<Album> = emptyList()
        override suspend fun getAlbum(id: Int, cacheControl: String?): Album = throw NotImplementedError()
        override suspend fun createAlbum(album: com.misw.vinilos.data.models.AlbumRequest): Album = throw NotImplementedError()
        override suspend fun addTrack(albumId: Int, track: TrackRequest): Track = throw NotImplementedError()
        override suspend fun getMusicians() = emptyList<com.misw.vinilos.data.models.Performer>()
        override suspend fun getBands() = emptyList<com.misw.vinilos.data.models.Performer>()
        override suspend fun getMusician(id: Int) = throw NotImplementedError()
        override suspend fun getBand(id: Int) = throw NotImplementedError()
        override suspend fun getCollectors(): List<Collector> = emptyList()
        override suspend fun getCollector(id: Int): Collector = throw NotImplementedError()
    })

    @Test
    fun create_retornaAddTrackViewModelCuandoSePideEsaClase() {
        val factory = AddTrackViewModelFactory(repository)

        val viewModel = factory.create(AddTrackViewModel::class.java)

        assertEquals(AddTrackViewModel::class.java, viewModel::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun create_lanzaExcepcionCuandoLaClaseNoEsSoportada() {
        val factory = AddTrackViewModelFactory(repository)

        factory.create(UnsupportedViewModel::class.java)
    }

    @Test
    fun create_instanciasDistintasSonIndependientes() {
        val factory = AddTrackViewModelFactory(repository)

        val vm1 = factory.create(AddTrackViewModel::class.java)
        val vm2 = factory.create(AddTrackViewModel::class.java)

        assert(vm1 !== vm2) { "Factory debe crear instancias distintas" }
    }

    class UnsupportedViewModel : ViewModel()
}
