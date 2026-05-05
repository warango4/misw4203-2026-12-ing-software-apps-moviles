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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class CollectorDetailViewModel(
    private val repository: CollectorRepository,
    private val albumRepository: AlbumRepository,
    private val collectorId: Int
) : ViewModel() {

    private companion object {
        private const val TAG = "CollectorDetailVM"
    }

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
                Log.i(TAG, "fetchCollector start id=$collectorId")
                val detail = repository.getCollector(collectorId)
                _collector.value = detail

                val embeddedAlbums = detail.collectorAlbums
                    .orEmpty()
                    .mapNotNull { it.album }

                if (embeddedAlbums.isNotEmpty()) {
                    Log.i(TAG, "fetchCollector embeddedAlbums count=${embeddedAlbums.size}")
                    _albums.value = embeddedAlbums
                } else {
                    val albumIds = detail.collectorAlbums
                        .orEmpty()
                        .map { it.albumId }
                        .distinct()

                    if (albumIds.isEmpty()) {
                        _albums.value = emptyList()
                    } else {
                        Log.i(TAG, "fetchCollector fallbackAlbumsFetch idsCount=${albumIds.size}")
                        val albumDetails = coroutineScope {
                            albumIds.map { id ->
                                async {
                                    try {
                                        albumRepository.getAlbum(id)
                                    } catch (e: Exception) {
                                        Log.w(TAG, "fetchCollector albumFetchFailure albumId=$id message=${e.message}")
                                        null
                                    }
                                }
                            }.awaitAll().filterNotNull()
                        }
                        _albums.value = albumDetails
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "fetchCollector failure message=${e.message}", e)
                _error.value = "No fue posible cargar el detalle del coleccionista"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

