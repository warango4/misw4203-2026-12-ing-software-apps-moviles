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
        Log.d("AlbumViewModel", "fetchAlbums called")
        viewModelScope.launch {
            try {
                Log.d("AlbumViewModel", "Fetching albums from repository")
                val result = repository.getAlbums()
                Log.d("AlbumViewModel", "Successfully fetched ${result.size} albums")
                _albums.value = result
            } catch (e: Exception) {
                Log.e("AlbumViewModel", "Error fetching albums: ${e.message}", e)
                _error.value = e.message
            }
        }
    }
}