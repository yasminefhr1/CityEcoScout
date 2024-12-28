package ma.ensa.projet.models

data class FavoriteRequest(
    val userId: Long,
    val placeId: Long
)