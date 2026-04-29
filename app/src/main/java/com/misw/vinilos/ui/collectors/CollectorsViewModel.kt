package com.misw.vinilos.ui.collectors

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.repository.CollectorRepository
import kotlinx.coroutines.launch

class CollectorsViewModel(private val repository: CollectorRepository) : ViewModel() {

    private val _collectors = MutableLiveData<List<Collector>>()
    val collectors: LiveData<List<Collector>> = _collectors

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun fetchCollectors() {
        if (_isLoading.value == true) return

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = repository.getCollectors().sortedBy { it.name }
                _collectors.value = result
                Log.d("CollectorsViewModel", "fetchCollectors: success count=${result.size}")
            } catch (e: Exception) {
                _error.value = "No fue posible cargar coleccionistas"
                Log.e("CollectorsViewModel", "fetchCollectors: failure message=${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

