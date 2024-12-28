package ma.ensa.projet.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ma.ensa.projet.models.Place
import ma.ensa.projet.retrofit.FavorisApi

class FavorisViewModel : ViewModel() {
    private val favorisApi = FavorisApi.create()

    private val _favorites = MutableStateFlow<List<Place>>(emptyList())
    val favorites: StateFlow<List<Place>> = _favorites

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadFavorites(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = favorisApi.getUserFavorites(userId)
                if (response.isSuccessful) {
                    val favoritesList = response.body() ?: emptyList()
                    _favorites.value = favoritesList
                    Log.d("FavorisViewModel", "Loaded ${favoritesList.size} favorites for user $userId")
                } else {
                    _error.value = "Erreur lors du chargement des favoris: ${response.code()}"
                    Log.e("FavorisViewModel", "Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _error.value = "Erreur de connexion: ${e.message}"
                Log.e("FavorisViewModel", "Error loading favorites", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}