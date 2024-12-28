package ma.ensa.projet.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val nom: String,
    val prenom: String,
    val email: String,
    val username: String,
    val photo: String?,
    val pays: String?,
    val password: String? = null
)