package ma.ensa.projet.models

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val id: Long,
    val place_name: String,
    val address: String,
    val latitude: Double,  // Changé en Double
    val longitude: Double, // Changé en Double
    val rating: Int,
    val category: String,
    val photo_url: String,
    val flag_url: String,
    val types: String,
    val user_ratings_total: Int,
    val country: String
)