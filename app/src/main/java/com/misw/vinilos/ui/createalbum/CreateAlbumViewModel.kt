package com.misw.vinilos.ui.createalbum

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.models.AlbumRequest
import com.misw.vinilos.data.repository.AlbumRepository
import com.misw.vinilos.util.HttpErrorMapper
import com.misw.vinilos.util.SingleLiveEvent
import kotlinx.coroutines.launch

class CreateAlbumViewModel(private val repository: AlbumRepository) : ViewModel() {

    private val _albumCreated = SingleLiveEvent<Album?>()
    val albumCreated: LiveData<Album?> get() = _albumCreated

    private val _error = SingleLiveEvent<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun createAlbum(
        name: String,
        cover: String,
        releaseDate: String,
        description: String,
        genre: String,
        recordLabel: String
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = AlbumRequest(name, cover, releaseDate, description, genre, recordLabel)
                val result = repository.createAlbum(request)
                Log.d("CreateAlbumViewModel", "createAlbum: success id=${result.id}")
                _albumCreated.value = result
            } catch (e: Exception) {
                Log.e("CreateAlbumViewModel", "createAlbum: failure", e)
                _error.value = HttpErrorMapper.toUserMessage(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
