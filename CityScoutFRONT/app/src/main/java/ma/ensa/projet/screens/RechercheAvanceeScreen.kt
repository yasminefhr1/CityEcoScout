package ma.ensa.projet.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ma.ensa.projet.R
import ma.ensa.projet.components.AppTopBar
import ma.ensa.projet.components.BottomNavigationBar
import ma.ensa.projet.components.DrawerMenu
import ma.ensa.projet.components.PlaceCard
import ma.ensa.projet.viewModel.PlaceViewModel
import ma.ensa.projet.viewModel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SearchScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    placeViewModel: PlaceViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val isDarkTheme = themeViewModel.isDarkTheme.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedCountry by remember { mutableStateOf<String?>(null) }
    var selectedRating by remember { mutableStateOf<Float?>(null) }

    val places = placeViewModel.places.value
    val categories = places.groupBy { it.category }

    val countries = LocalContext.current.resources.getStringArray(R.array.countries).toList()

    // Rating options
    val ratingOptions = listOf(1f, 2f, 3f, 4f, 5f)

    DrawerMenu(
        navController = navController,
        isDarkTheme = isDarkTheme,
        drawerState = drawerState,
        onToggleTheme = { themeViewModel.toggleTheme() }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Explore",
                    scope = scope,
                    drawerState = drawerState
                )
            },
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(if (isDarkTheme.value) Color.Black else Color.White)
            ) {
                // Modern Search Bar
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(60.dp)
                        .shadow(8.dp, RoundedCornerShape(30.dp)),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            placeholder = {
                                Text(
                                    "What are you looking for?",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                // Filters Section
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Categories
                    items(categories.entries.toList()) { (category, placesInCategory) ->
                        CategoryChip(
                            title = category,
                            icon = when (category) {
                                "restaur" -> Icons.Rounded.RestaurantMenu
                                "Hotel" -> Icons.Rounded.Hotel
                                else -> Icons.Rounded.Store
                            },
                            count = placesInCategory.size,
                            isSelected = selectedCategory == category,
                            onClick = {
                                selectedCategory =
                                    if (selectedCategory == category) null else category
                            }
                        )
                    }
                }

                // Country Filter
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(countries) { country ->
                        FilterChip(
                            selected = selectedCountry == country,
                            onClick = {
                                selectedCountry = if (selectedCountry == country) null else country
                            },
                            label = { Text(country) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Place,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }

                // Rating Filter
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ratingOptions) { rating ->
                        FilterChip(
                            selected = selectedRating == rating,
                            onClick = {
                                selectedRating = if (selectedRating == rating) null else rating
                            },
                            label = { Text("${rating.toInt()}+ Stars") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }

                // Results Section
                val filteredPlaces = places.filter { place ->
                    val matchesSearch = place.place_name.contains(searchQuery, ignoreCase = true) ||
                            place.types.contains(searchQuery, ignoreCase = true)
                    val matchesCategory =
                        selectedCategory == null || place.category == selectedCategory
                    val matchesCountry = selectedCountry == null || place.country == selectedCountry
                    val matchesRating =
                        selectedRating == null || place.rating >= (selectedRating ?: 0f)

                    matchesSearch && matchesCategory && matchesCountry && matchesRating
                }

                AnimatedContent(
                    targetState = filteredPlaces.isEmpty(),
                    transitionSpec = {
                        fadeIn() with fadeOut()
                    }
                ) { isEmpty ->
                    if (isEmpty) {
                        EmptySearchResult()
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            items(filteredPlaces) { place ->
                                PlaceCard(
                                    place = place,
                                    navController = navController,
                                    placeViewModel = placeViewModel,
                                    userId = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    title: String,
    icon: ImageVector,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface,
        modifier = Modifier.shadow(
            elevation = if (isSelected) 8.dp else 2.dp,
            shape = RoundedCornerShape(50.dp)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "$title ($count)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EmptySearchResult() {
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
                text = "Try modifying your search criteria",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}