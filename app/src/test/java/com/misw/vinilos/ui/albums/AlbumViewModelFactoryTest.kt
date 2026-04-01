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
}
