package com.misw.vinilos.ui.addtrack

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.vinilos.data.models.Track
import com.misw.vinilos.data.models.TrackRequest
import com.misw.vinilos.data.repository.AlbumRepository
import kotlinx.coroutines.launch

class AddTrackViewModel(private val repository: AlbumRepository) : ViewModel() {

    private val _trackAdded = MutableLiveData<Track?>()
    val trackAdded: LiveData<Track?> get() = _trackAdded

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun addTrack(albumId: Int, name: String, duration: String) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val request = TrackRequest(name, duration)
                val result = repository.addTrack(albumId, request)
                Log.d("AddTrackViewModel", "addTrack: success id=${result.id}")
                _trackAdded.value = result
            } catch (e: Exception) {
                Log.e("AddTrackViewModel", "addTrack: failure", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
