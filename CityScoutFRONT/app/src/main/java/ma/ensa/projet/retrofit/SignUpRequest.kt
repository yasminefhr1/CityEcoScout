package ma.ensa.projet.retrofit

import kotlinx.serialization.Serializable



@Serializable
data class SignUpRequest(
    val nom: String,
    val prenom: String,
    val pays: String,
    val email: String,
    val username: String,
    val password: String,
    val confirmPassword: String,
)

