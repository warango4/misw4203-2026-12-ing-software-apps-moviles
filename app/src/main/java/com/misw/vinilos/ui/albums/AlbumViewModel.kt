package com.misw.vinilos.ui.albums

import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.repository.AlbumRepository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AlbumViewModel(private val repository: AlbumRepository) : ViewModel() {

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = _albums

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchAlbums() {
        Log.d("AlbumViewModel", "fetchAlbums: request started")
        viewModelScope.launch {
            try {
                val result = repository.getAlbums()
                Log.d("AlbumViewModel", "fetchAlbums: success count=${result.size}")
                _albums.value = result
            } catch (e: Exception) {
                Log.e("AlbumViewModel", "fetchAlbums: failure message=${e.message}", e)
                _error.value = e.message
            }
        }
    }
}