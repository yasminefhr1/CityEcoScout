package ma.ensa.projet.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ma.ensa.projet.models.Post
import ma.ensa.projet.models.PostType
import ma.ensa.projet.models.User

@Composable
fun PostCard(
    post: Post,
    currentUserId: Long,
    onLikeClick: (Post) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // User Header
            UserHeader(post.utilisateur.username)

            // Post Image
            post.imageBytes?.let { bytes ->
                PostImage(bytes)
            }

            // Post Content
            if (post.content.isNotEmpty()) {
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Post Type Chip
            PostTypeChip(post.type)

            // Like Section
            LikeSection(
                likes = post.likes,
                currentUserId = currentUserId,
                onLikeClick = { onLikeClick(post) }
            )
        }
    }
}

@Composable
private fun UserHeader(username: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = "User Avatar",
                modifier = Modifier.padding(8.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = username,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PostImage(imageBytes: ByteArray) {
    val imageBitmap = remember(imageBytes) {
        val options = BitmapFactory.Options().apply {
            inSampleSize = 2 // Réduire la taille de l'image
        }
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)?.asImageBitmap()
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun PostTypeChip(type: PostType) {
    AssistChip(
        onClick = { },
        label = { Text(type.name) },
        leadingIcon = {
            Icon(
                imageVector = when (type) {
                    PostType.BON_PLAN -> Icons.Rounded.Star
                    PostType.EVENEMENT -> Icons.Rounded.Event
                    PostType.CONSEIL -> Icons.Rounded.Lightbulb
                    PostType.QUESTION -> TODO()
                },
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    )
}

@Composable
private fun LikeSection(
    likes: List<User>,
    currentUserId: Long,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val isLiked = likes.any { it.id == currentUserId }

        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = if (isLiked) "Unlike" else "Like",
                tint = if (isLiked) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "${likes.size} likes",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
