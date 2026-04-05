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
class PerformerViewModel(private val repository: PerformerRepository) : ViewModel() {
    private val _performers = MutableLiveData<List<Performer>>()
    val performers: LiveData<List<Performer>> = _performers
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    fun fetchPerformers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("PerformerViewModel", "Fetching performers")
                val response = repository.getPerformers()
                _performers.value = response
                _error.value = null
            } catch (e: Exception) {
                Log.e("PerformerViewModel", "Error fetching performers", e)
                _error.value = "Error al obtener el listado de artistas"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
class PerformerViewModelFactory(private val repository: PerformerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerformerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PerformerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
