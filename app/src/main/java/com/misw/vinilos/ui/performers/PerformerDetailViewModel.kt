package com.misw.vinilos.ui.performers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.misw.vinilos.data.models.Performer
import com.misw.vinilos.data.repository.PerformerRepository
import kotlinx.coroutines.launch

class PerformerDetailViewModel(
    private val repository: PerformerRepository,
    private val performerId: Int,
    private val isBand: Boolean
) : ViewModel() {

    private companion object {
        private const val TAG = "PerformerDetailVM"
    }

    private val _performer = MutableLiveData<Performer?>(null)
    val performer: LiveData<Performer?> = _performer

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchPerformerDetail()
    }

    private fun fetchPerformerDetail() {
        viewModelScope.launch {
            if (_isLoading.value == true) return@launch

            _isLoading.value = true
            try {
                Log.d(TAG, "fetchPerformer: request started performerId=$performerId isBand=$isBand")
                val response = repository.getPerformer(performerId, isBand)
                _performer.value = response
                _error.value = null
            } catch (e: Exception) {
                Log.e(TAG, "fetchPerformer: failure performerId=$performerId message=${e.message}", e)
                _error.value = "Error al obtener el detalle del artista"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class PerformerDetailViewModelFactory(
    private val repository: PerformerRepository,
    private val performerId: Int,
    private val isBand: Boolean
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerformerDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PerformerDetailViewModel(repository, performerId, isBand) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
