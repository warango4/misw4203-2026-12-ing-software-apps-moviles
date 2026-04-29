package com.misw.vinilos.ui.albums

import androidx.lifecycle.ViewModel
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.data.repository.AlbumRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AlbumViewModelFactoryTest {

    private val repository = AlbumRepository(object : VinilosApiService {
        override suspend fun getAlbums(): List<Album> = emptyList()
        override suspend fun getAlbum(id: Int): Album = throw NotImplementedError()
        override suspend fun getMusicians() = emptyList<com.misw.vinilos.data.models.Performer>()
        override suspend fun getBands() = emptyList<com.misw.vinilos.data.models.Performer>()
        override suspend fun getMusician(id: Int) = throw NotImplementedError()
        override suspend fun getBand(id: Int) = throw NotImplementedError()
        override suspend fun getCollectors(): List<Collector> = emptyList()
        override suspend fun getCollector(id: Int): Collector = throw NotImplementedError()
    })

    @Test
    fun create_retornaAlbumViewModelCuandoSePideEsaClase() {
        val factory = AlbumViewModelFactory(repository)

        val viewModel = factory.create(AlbumViewModel::class.java)

        assertEquals(AlbumViewModel::class.java, viewModel::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun create_lanzaExcepcionCuandoLaClaseNoEsSoportada() {
        val factory = AlbumViewModelFactory(repository)

        factory.create(UnsupportedViewModel::class.java)
    }

    class UnsupportedViewModel : ViewModel()

    @Test
    fun create_instanciasDistintasSonIndependientes() {
        val factory = AlbumViewModelFactory(repository)

        val vm1 = factory.create(AlbumViewModel::class.java)
        val vm2 = factory.create(AlbumViewModel::class.java)

        assert(vm1 !== vm2) { "Factory debe crear instancias distintas" }
    }
}
