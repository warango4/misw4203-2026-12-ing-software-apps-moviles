package com.misw.vinilos.ui.albums

import androidx.lifecycle.ViewModel
import com.misw.vinilos.data.models.Album
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
            override suspend fun getCollectors() = throw NotImplementedError()
        override suspend fun getMusician(id: Int) = throw NotImplementedError()
        override suspend fun getBand(id: Int) = throw NotImplementedError()
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
    fun create_withAlbumViewModelClass_returnsAlbumViewModel() {
        val fakeRepository = AlbumRepository(object : com.misw.vinilos.data.network.VinilosApiService {
            override suspend fun getAlbums(): List<com.misw.vinilos.data.models.Album> = emptyList()
            override suspend fun getAlbum(id: Int): com.misw.vinilos.data.models.Album = throw NotImplementedError()
            override suspend fun getMusicians() = emptyList<com.misw.vinilos.data.models.Performer>()
            override suspend fun getBands() = emptyList<com.misw.vinilos.data.models.Performer>()
            override suspend fun getCollectors() = throw NotImplementedError()
            override suspend fun getMusician(id: Int) = throw NotImplementedError()
            override suspend fun getBand(id: Int) = throw NotImplementedError()
        })
        val factory = AlbumViewModelFactory(fakeRepository)
        val viewModel = factory.create(AlbumViewModel::class.java)
        assertEquals(AlbumViewModel::class.java, viewModel::class.java)
    }
}
