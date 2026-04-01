package com.misw.vinilos.ui.albums

import com.misw.vinilos.data.repository.AlbumRepository
import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AlbumViewModelFactory(private val repository: AlbumRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("AlbumViewModelFactory", "Creating ViewModel block with class: ${modelClass.simpleName}")
        if (modelClass.isAssignableFrom(AlbumViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumViewModel(repository) as T
        }
        Log.e("AlbumViewModelFactory", "Unknown ViewModel class")
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}