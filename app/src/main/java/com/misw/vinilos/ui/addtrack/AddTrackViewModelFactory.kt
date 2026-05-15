package com.misw.vinilos.ui.addtrack

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.misw.vinilos.data.repository.AlbumRepository

class AddTrackViewModelFactory(private val repository: AlbumRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("AddTrackViewModelFactory", "create: modelClass=${modelClass.simpleName}")
        if (modelClass.isAssignableFrom(AddTrackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddTrackViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
