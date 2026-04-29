package com.misw.vinilos.ui.collectors

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.repository.AlbumRepository
import com.misw.vinilos.data.repository.CollectorRepository
import kotlinx.coroutines.launch

class CollectorDetailViewModel(
    private val repository: CollectorRepository,
    private val albumRepository: AlbumRepository,
    private val collectorId: Int
) : ViewModel() {

    private val _collector = MutableLiveData<Collector?>(null)
    val collector: LiveData<Collector?> = _collector

    private val _albums = MutableLiveData<List<Album>>(emptyList())
    val albums: LiveData<List<Album>> = _albums

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun fetchCollector() {
        if (_isLoading.value == true) return
        if (collectorId <= 0) {
            _error.value = "Collector inválido"
            return
        }

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                Log.d("CollectorDetailViewModel", "fetchCollector: id=$collectorId")
                val detail = repository.getCollector(collectorId)
                _collector.value = detail

                // HU06: collectorAlbums trae solo ids de álbum. Consultamos cada álbum para mostrar cards reales.
                val albumIds = detail.collectorAlbums
                    .orEmpty()
                    .map { it.albumId }
                    .distinct()

                val albumDetails = albumIds.mapNotNull { id ->
                    try {
                        albumRepository.getAlbum(id)
                    } catch (e: Exception) {
                        Log.e("CollectorDetailViewModel", "fetchCollector: album fetch failure albumId=$id", e)
                        null
                    }
                }
                _albums.value = albumDetails
            } catch (e: Exception) {
                Log.e("CollectorDetailViewModel", "fetchCollector: failure message=${e.message}", e)
                _error.value = "No fue posible cargar el detalle del coleccionista"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

