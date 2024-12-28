package ma.ensa.projet.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import ma.ensa.projet.viewModel.PlaceViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.blur
import androidx.navigation.NavController
import ma.ensa.projet.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    placeViewModel: PlaceViewModel,
    onNavigateBack: () -> Unit
) {
    val selectedPlace = placeViewModel.selectedPlace.value
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },  // Titre vide pour un look plus moderne
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(8.dp)
                            .shadow(4.dp, CircleShape)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->
        selectedPlace?.let { place ->
            Box(modifier = Modifier.fillMaxSize()) {
                // Image de fond avec un effet de parallaxe
                AsyncImage(
                    model = place.photo_url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .blur(radius = 16.dp),
                    contentScale = ContentScale.Crop
                )

                // Contenu principal
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Image principale dans une carte arrondie
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .height(300.dp)
                            .shadow(20.dp, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        AsyncImage(
                            model = place.photo_url,
                            contentDescription = "${place.place_name} image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Carte des détails avec design moderne
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .offset(y = (-30).dp),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth()
                        ) {
                            // En-tête avec nom et catégorie
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = place.place_name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (place.category) {
                                                "restaur" -> Icons.Default.RestaurantMenu
                                                "Hotel" -> Icons.Default.Hotel
                                                else -> Icons.Default.Store
                                            },
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = when (place.category) {
                                                "restaur" -> "Restaurant"
                                                "Hotel" -> "Hôtel"
                                                else -> "Commerce"
                                            },
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Note avec design moderne
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${place.rating}",
                                            style = MaterialTheme.typography.headlineLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Row {
                                            repeat(5) { index ->
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = if (index < place.rating)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                    Divider(
                                        modifier = Modifier
                                            .height(40.dp)
                                            .width(1.dp)
                                    )
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${place.user_ratings_total}",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Reviews",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Localisation avec design moderne
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = place.flag_url,
                                            contentDescription = "Drapeau ${place.country}",
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .border(
                                                    1.dp,
                                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = place.country,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = place.address,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Carte Google Maps avec coins arrondis
                                    val location = LatLng(place.latitude.toDouble(), place.longitude.toDouble())
                                    val cameraPositionState = rememberCameraPositionState {
                                        position = CameraPosition.fromLatLngZoom(location, 14f)
                                    }

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        GoogleMap(
                                            modifier = Modifier.fillMaxSize(),
                                            cameraPositionState = cameraPositionState
                                        ) {
                                            Marker(
                                                state = MarkerState(position = location),
                                                title = place.place_name,
                                                snippet = place.address
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}