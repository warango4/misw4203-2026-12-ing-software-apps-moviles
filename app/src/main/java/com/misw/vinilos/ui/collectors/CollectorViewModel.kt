package com.misw.vinilos.ui.collectors

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.repository.CollectorRepository
import kotlinx.coroutines.launch

class CollectorViewModel(private val repository: CollectorRepository) : ViewModel() {

    private val _collectors = MutableLiveData<List<Collector>>()
    val collectors: LiveData<List<Collector>> get() = _collectors

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchCollectors()
    }

    fun fetchCollectors() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getCollectors()
                _collectors.value = result
                _error.value = null
                Log.d("CollectorViewModel", "Successfully fetched ${result.size} collectors.")
            } catch (e: Exception) {
                Log.e("CollectorViewModel", "Error fetching collectors: ${e.message}", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class CollectorViewModelFactory(private val repository: CollectorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CollectorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

