package ma.ensa.projet.retrofit



import kotlinx.serialization.Serializable

@Serializable
data class Utilisateur(
    val id: Long,
    val nom: String,
    val prenom: String,
    val pays: String,
    val email: String,
    val username: String,
    val photo: String?
)

