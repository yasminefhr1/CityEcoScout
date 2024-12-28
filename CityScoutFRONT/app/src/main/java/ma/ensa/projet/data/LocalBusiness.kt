package ma.ensa.projet.models

data class LocalBusiness(
    val id: String,
    val name: String,
    val address: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String
)
