package ma.ensa.projet.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ma.ensa.projet.models.FavoriteRequest
import ma.ensa.projet.models.Place
import ma.ensa.projet.retrofit.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlaceViewModel : ViewModel() {
    private val _selectedPlace = mutableStateOf<Place?>(null)
    val selectedPlace: State<Place?> = _selectedPlace

    fun selectPlace(place: Place) {
        _selectedPlace.value = place
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.11.107:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(PlaceApi::class.java)
    private val favorisApi = retrofit.create(FavorisApi::class.java)

    private val _places = mutableStateOf<List<Place>>(emptyList())
    val places: State<List<Place>> = _places

    init {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Début du chargement des données")
                val result = api.getPlaces()
                Log.d("HomeViewModel", "Données reçues: ${result.size} places")
                _places.value = result
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erreur lors du chargement", e)
            }
        }
    }

    private suspend fun refreshPlace(placeId: Long) {
        try {
            val updatedPlace = PlaceApi.getPlaceById(placeId)
            if (updatedPlace != null) {
                _places.value = _places.value.map { place ->
                    if (place.id == placeId) updatedPlace else place
                }
                Log.d("PlaceViewModel", "Place refreshed successfully: ${updatedPlace.user_ratings_total} ratings")
            }
        } catch (e: Exception) {
            Log.e("PlaceViewModel", "Error refreshing place", e)
        }
    }

    // Fonction pour récupérer le statut du favori
    suspend fun getFavoriteStatus(userId: Long, placeId: Long): Boolean {
        return try {
            val response = favorisApi.getFavoriteStatus(userId, placeId)
            response.isSuccessful && (response.body() ?: false)
        } catch (e: Exception) {
            Log.e("PlaceViewModel", "Network error getting favorite status", e)
            false
        }
    }

    // Fonction pour basculer l'état du favori dans la table favoris
    fun toggleFavorite(place: Place, userId: Long) {
        viewModelScope.launch {
            try {
                val request = FavoriteRequest(userId, place.id)
                val response = favorisApi.toggleFavorite(request)

                if (response.isSuccessful) {
                    Log.d("PlaceViewModel", "Toggle favori réussi")
                    refreshPlace(place.id)
                } else {
                    Log.e("PlaceViewModel", "Erreur lors du toggle favori: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("PlaceViewModel", "Erreur réseau lors du toggle favori", e)
            }
        }
    }

}