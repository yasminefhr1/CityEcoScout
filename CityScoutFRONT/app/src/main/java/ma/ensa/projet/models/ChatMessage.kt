package ma.ensa.projet.models

import android.graphics.Bitmap

data class ChatMessage(
    val isUser: Boolean,
    val content: String,
    val imageUri: String = "",
    val bitmap: Bitmap? = null
)