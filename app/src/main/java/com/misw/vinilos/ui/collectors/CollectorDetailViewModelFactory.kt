package com.misw.vinilos.ui.collectors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.misw.vinilos.data.repository.AlbumRepository
import com.misw.vinilos.data.repository.CollectorRepository

class CollectorDetailViewModelFactory(
    private val repository: CollectorRepository,
    private val albumRepository: AlbumRepository,
    private val collectorId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectorDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CollectorDetailViewModel(repository, albumRepository, collectorId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

