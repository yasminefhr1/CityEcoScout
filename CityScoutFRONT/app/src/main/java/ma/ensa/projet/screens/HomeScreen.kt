package ma.ensa.projet.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ma.ensa.projet.components.*
import ma.ensa.projet.ui.theme.CityScoutTheme
import ma.ensa.projet.viewModel.PlaceViewModel
import ma.ensa.projet.viewModel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    placeViewModel: PlaceViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getLong("user_id", -1L)
    val isDarkTheme = themeViewModel.isDarkTheme.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val places = placeViewModel.places.value

    DrawerMenu(
        navController = navController,
        isDarkTheme = isDarkTheme,
        drawerState = drawerState,
        onToggleTheme = { themeViewModel.toggleTheme() }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Home",
                    scope = scope,
                    drawerState = drawerState
                )
            },
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) { padding ->
            CityScoutTheme(darkTheme = isDarkTheme.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isDarkTheme.value) Color.Black else Color.White)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        item {
                            WelcomeSection(isDarkTheme.value)
                        }

                        item {
                            QuickFilters()
                        }

                        item {
                            CategorySection(isDarkTheme.value)
                        }

                        item {
                            SectionTitle(
                                title = "Sustainable Discoveries",
                                subtitle = "Eco-friendly places around the world"
                            )
                        }

                        items(places) { place ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + expandVertically(),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                PlaceCard(
                                    place = place,
                                    navController = navController,
                                    placeViewModel = placeViewModel,
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
private fun WelcomeSection(isDarkTheme: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Hello 👋",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Discover the best sustainable places around you",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickFilters() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        item {
            FilterChip(
                selected = true,
                onClick = { },
                label = { Text("All") },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Apps,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
        item {
            FilterChip(
                selected = false,
                onClick = { },
                label = { Text("Popular") },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
        item {
            FilterChip(
                selected = false,
                onClick = { },
                label = { Text("New") },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.NewReleases,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun CategorySection(isDarkTheme: Boolean) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        SectionTitle(
            title = "Categories",
            subtitle = "Explore different types of places"
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            item {
                CategoryCard(
                    icon = Icons.Rounded.Restaurant,
                    title = "Restaurant",
                )
            }
            item {
                CategoryCard(
                    icon = Icons.Rounded.Hotel,
                    title = "Hotel"
                )
            }
            item {
                CategoryCard(
                    icon = Icons.Rounded.Store,
                    title = "Store"
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}
