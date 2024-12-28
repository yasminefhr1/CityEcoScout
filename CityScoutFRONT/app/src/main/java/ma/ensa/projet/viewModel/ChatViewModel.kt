package ma.ensa.projet.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ma.ensa.projet.data.PlaceRepository
import ma.ensa.projet.models.ChatMessage
import ma.ensa.projet.models.Place


class ChatViewModel : ViewModel() {
    private val placeRepository = PlaceRepository()
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    // Liste des mots-clés à détecter pour la recherche de lieux
    private val placeSearchKeywords = listOf(
        "hotel", "hôtel", "hotels", "hôtels", "hébergement",
        "restaurant", "restaurants", "resto", "restos", "restauration",
        "store", "magasin", "magasins", "boutique", "boutiques", "commerce"
    )

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyDjEbZDIm136ccfSpUiSwvrCUXtJENxJEU"
        )
    }

    fun sendMessage(message: String, imageUri: String = "", bitmap: Bitmap? = null) {
        viewModelScope.launch {
            _messages.value = _messages.value + ChatMessage(
                isUser = true,
                content = message,
                imageUri = imageUri,
                bitmap = bitmap
            )

            try {
                // Vérifie si le message contient un des mots-clés de recherche de lieux
                if (placeSearchKeywords.any { keyword ->
                        message.contains(keyword, ignoreCase = true)
                    }) {
                    // Utilise PlaceRepository pour chercher les lieux
                    val places = placeRepository.searchPlaces(message)
                    val response = formatPlacesResponse(places)
                    _messages.value = _messages.value + ChatMessage(
                        isUser = false,
                        content = response
                    )
                } else {
                    // Utilise Gemini pour les autres types de questions
                    val response = generativeModel.generateContent(message)
                    _messages.value = _messages.value + ChatMessage(
                        isUser = false,
                        content = response.text ?: "Désolé, je n'ai pas pu générer de réponse."
                    )
                }
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(
                    isUser = false,
                    content = "Désolé, une erreur s'est produite: ${e.message}"
                )
            }
        }
    }

    private fun formatPlacesResponse(places: List<Place>): String {
        if (places.isEmpty()) return "No establishments found."

        return buildString {
            appendLine("Here are the establishments found:")
            places.forEach { place ->
                appendLine("\n📍 ${place.place_name}")
                appendLine("⭐ Note: ${place.rating}/5")
                appendLine("🏷 ${place.types}")
                appendLine("📫 ${place.address}")
            }
        }
    }
}