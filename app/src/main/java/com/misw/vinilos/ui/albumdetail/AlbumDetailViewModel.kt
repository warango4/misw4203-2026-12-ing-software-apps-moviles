package com.misw.vinilos.ui.albumdetail
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.repository.AlbumRepository
import kotlinx.coroutines.launch
class AlbumDetailViewModel(private val repository: AlbumRepository, private val albumId: Int) : ViewModel() {
    private val _album = MutableLiveData<Album>()
    val album: LiveData<Album> = _album
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    init {
        fetchAlbum()
    }
    private fun fetchAlbum() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("AlbumDetailViewModel", "Fetching album $albumId")
                val response = repository.getAlbum(albumId)
                _album.value = response
                Log.d("AlbumDetailViewModel", "Album fetched: ${response.name}")
            } catch (e: Exception) {
                Log.e("AlbumDetailViewModel", "Error fetching album", e)
                _error.value = "Error al obtener el detalle del álbum"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
class AlbumDetailViewModelFactory(private val repository: AlbumRepository, private val albumId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumDetailViewModel(repository, albumId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
