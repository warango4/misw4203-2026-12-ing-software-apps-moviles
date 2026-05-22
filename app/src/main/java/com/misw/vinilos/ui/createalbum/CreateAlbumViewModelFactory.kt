package com.misw.vinilos.ui.createalbum

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.misw.vinilos.data.repository.AlbumRepository

class CreateAlbumViewModelFactory(private val repository: AlbumRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("CreateAlbumViewModelFactory", "create: modelClass=${modelClass.simpleName}")
        if (modelClass.isAssignableFrom(CreateAlbumViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateAlbumViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
