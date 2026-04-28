package com.misw.vinilos.ui.collectors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.misw.vinilos.data.repository.CollectorRepository

class CollectorsViewModelFactory(private val repository: CollectorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectorsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CollectorsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

