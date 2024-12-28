package ma.ensa.projet.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.navigation.NavController
import ma.ensa.projet.models.Place
import ma.ensa.projet.viewModel.PlaceViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlaceCard(
    place: Place,
    navController: NavController,
    placeViewModel: PlaceViewModel,
    userId: Long
) {
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = place.id, key2 = userId) {
        isFavorite = placeViewModel.getFavoriteStatus(userId, place.id)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) // Customizing shadow color
            )
            .clickable {
                placeViewModel.selectPlace(place)
                navController.navigate("detail")
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box {
            // Image
            AsyncImage(
                model = place.photo_url,
                contentDescription = place.place_name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Category chip
            Surface(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopStart),
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = when (place.category) {
                            "restaur" -> Icons.Rounded.RestaurantMenu
                            "Hotel" -> Icons.Rounded.Hotel
                            else -> Icons.Rounded.Store
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = place.category,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Favorite button
            IconButton(
                onClick = {
                    placeViewModel.toggleFavorite(place, userId)
                    isFavorite = !isFavorite
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        CircleShape
                    )
            ) {
                AnimatedContent(
                    targetState = isFavorite,
                    transitionSpec = {
                        scaleIn() with scaleOut()
                    }
                ) { favorite ->
                    Icon(
                        imageVector = if (favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (favorite) Color.Red else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = place.place_name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = place.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < place.rating) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (index < place.rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${place.user_ratings_total})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // View details button
                TextButton(
                    onClick = {
                        placeViewModel.selectPlace(place)
                        navController.navigate("detail")
                    }
                ) {
                    Text("View details")
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
