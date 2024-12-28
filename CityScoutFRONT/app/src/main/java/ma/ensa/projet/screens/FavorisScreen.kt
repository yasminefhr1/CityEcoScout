package ma.ensa.projet.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ma.ensa.projet.components.*
import ma.ensa.projet.ui.theme.CityScoutTheme
import ma.ensa.projet.viewModel.FavorisViewModel
import ma.ensa.projet.viewModel.ThemeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FavorisScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    userId: Long,
    favorisViewModel: FavorisViewModel = viewModel()
) {
    val isDarkTheme = themeViewModel.isDarkTheme.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val favorites by favorisViewModel.favorites.collectAsState()
    val isLoading by favorisViewModel.isLoading.collectAsState()
    val error by favorisViewModel.error.collectAsState()

    // Charger les favoris quand l'écran est créé
    LaunchedEffect(userId) {
        Log.d("FavorisScreen", "Loading favorites for user $userId")
        favorisViewModel.loadFavorites(userId)
    }

    DrawerMenu(
        navController = navController,
        isDarkTheme = isDarkTheme,
        drawerState = drawerState,
        onToggleTheme = { themeViewModel.toggleTheme() }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Favorites",
                    scope = scope,
                    drawerState = drawerState
                )
            },
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    error != null -> {
                        ErrorMessage(
                            message = error ?: "error",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    favorites.isEmpty() -> {
                        EmptyFavorites()
                    }
                    else -> {
                        LazyColumn {
                            items(favorites) { place ->
                                PlaceCard(
                                    place = place,
                                    navController = navController,
                                    placeViewModel = viewModel(),
                                    userId = userId
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorMessage(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}



@Composable
private fun EmptyFavorites(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SearchOff,
                    contentDescription = "No results",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "No results found",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Explore places and add them to your favorites!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}