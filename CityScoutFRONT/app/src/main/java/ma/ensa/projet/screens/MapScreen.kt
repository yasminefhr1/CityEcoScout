package ma.ensa.projet.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.StreetViewPanoramaView
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import ma.ensa.projet.components.AppTopBar
import ma.ensa.projet.components.BottomNavigationBar
import ma.ensa.projet.models.Place
import ma.ensa.projet.viewModel.PlaceViewModel
import ma.ensa.projet.viewModel.ThemeViewModel
import android.webkit.WebView
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.draw.alpha
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.StreetViewPanorama
import ma.ensa.projet.R
import ma.ensa.projet.components.DrawerMenu
import ma.ensa.projet.viewModel.PostViewModel
import ma.ensa.projet.viewModel.PostViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    placeViewModel: PlaceViewModel = viewModel()
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val places = placeViewModel.places.value
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedDistance by remember { mutableStateOf<Int?>(null) }
    var isFilterExpanded by remember { mutableStateOf(false) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showStreetView by remember { mutableStateOf(false) }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }

    val cameraPositionState = rememberCameraPositionState()
    var selectedCountry by remember { mutableStateOf<String?>(null) }
    val isDarkTheme = themeViewModel.isDarkTheme.collectAsState()
    val scrollState = rememberLazyListState()
    val popularCountries = LocalContext.current.resources.getStringArray(R.array.countries).toList()

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] / 1000
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                    scope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newCameraPosition(
                                CameraPosition.fromLatLngZoom(
                                    LatLng(it.latitude, it.longitude),
                                    15f
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    val filteredPlaces = places.filter { place ->
        val matchesSearch = searchQuery.isEmpty() ||
                place.place_name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null ||
                place.category == selectedCategory
        val matchesCountry = selectedCountry == null ||
                place.country == selectedCountry
        val matchesDistance = if (selectedDistance != null && userLocation != null) {
            val distance = calculateDistance(
                userLocation!!.latitude,
                userLocation!!.longitude,
                place.latitude.toDouble(),
                place.longitude.toDouble()
            )
            distance <= selectedDistance!!
        } else true

        matchesSearch && matchesCategory && matchesCountry && matchesDistance
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
                    title = "Explore the places",
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
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true)
                ) {
                    filteredPlaces.forEach { place ->
                        val position = LatLng(place.latitude.toDouble(), place.longitude.toDouble())
                        Marker(
                            state = MarkerState(position = position),
                            title = place.place_name,
                            snippet = "${place.category} - Note: ${place.rating}/5",
                            icon = BitmapDescriptorFactory.defaultMarker(
                                when (place.category) {
                                    "restaur" -> BitmapDescriptorFactory.HUE_GREEN
                                    "Hotel" -> BitmapDescriptorFactory.HUE_BLUE
                                    else -> BitmapDescriptorFactory.HUE_YELLOW
                                }
                            ),
                            onClick = { marker ->
                                selectedPlace = place
                                showBottomSheet = true
                                true
                            }
                        )
                    }
                }

                if (showStreetView && selectedPlace != null) {
                    PanoramaDialog(
                        place = selectedPlace!!,
                        onDismiss = { showStreetView = false }
                    )
                }

                if (showBottomSheet && selectedPlace != null) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showBottomSheet = false
                            selectedPlace = null
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = selectedPlace!!.place_name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = selectedPlace!!.address,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.Star, null)
                                Text(
                                    text = " ${selectedPlace!!.rating}/5",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = " (${selectedPlace!!.user_ratings_total} avis)",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    showStreetView = true
                                    showBottomSheet = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Rounded.Place,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Voir en 360°")
                            }

                            if (showStreetView && selectedPlace != null) {
                                PanoramaDialog(
                                    place = selectedPlace!!,
                                    onDismiss = {
                                        showStreetView = false
                                        selectedPlace = null
                                    }
                                )
                            }
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .fillMaxWidth(0.9f)
                        .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.8f),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search for a place") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Rounded.Search, null)
                            },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { isFilterExpanded = !isFilterExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isFilterExpanded) "Less filters" else "More filters")
                                Icon(
                                    if (isFilterExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                    null,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = isFilterExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState())
                                    .fillMaxWidth()
                                    .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.5f)
                                    .padding(vertical = 8.dp)
                            ) {
                                FilterSection(
                                    title = "",
                                    content = {
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(listOf(1, 10, 50, 100, 200, 500)) { distance ->
                                                FilterChip(
                                                    selected = selectedDistance == distance,
                                                    onClick = {
                                                        selectedDistance =
                                                            if (selectedDistance == distance) null else distance
                                                    },
                                                    label = { Text("${distance}km") }
                                                )
                                            }
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                FilterSection(
                                    title = "Categories",
                                    content = {
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            val categories = places.groupBy { it.category }
                                            items(categories.keys.toList()) { category ->
                                                FilterChip(
                                                    selected = selectedCategory == category,
                                                    onClick = {
                                                        selectedCategory =
                                                            if (selectedCategory == category) null else category
                                                    },
                                                    label = {
                                                        Text("$category (${categories[category]?.size})")
                                                    },
                                                    leadingIcon = {
                                                        Icon(
                                                            when (category) {
                                                                "restaur" -> Icons.Rounded.Restaurant
                                                                "Hotel" -> Icons.Rounded.Hotel
                                                                "store" -> Icons.Rounded.Store
                                                                else -> Icons.Rounded.Store
                                                            },
                                                            null,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                FilterSection(
                                    title = "Country",
                                    content = {
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(popularCountries) { country ->
                                                FilterChip(
                                                    selected = selectedCountry == country,
                                                    onClick = {
                                                        selectedCountry =
                                                            if (selectedCountry == country) null else country
                                                    },
                                                    label = { Text(country) }
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "${filteredPlaces.size} places found",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun PanoramaDialog(
    place: Place,
    onDismiss: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPanoramaReady by remember { mutableStateOf(false) }
    var isInitializing by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // Vérifier si les Google Play Services sont disponibles
    LaunchedEffect(Unit) {
        try {
            val availability = GoogleApiAvailability.getInstance()
            val resultCode = availability.isGooglePlayServicesAvailable(context)

            if (resultCode != ConnectionResult.SUCCESS) {
                errorMessage = "Google Play Services n'est pas disponible"
                Handler(Looper.getMainLooper()).postDelayed({
                    onDismiss()
                }, 2000)
                return@LaunchedEffect
            }
        } catch (e: Exception) {
            errorMessage = "Erreur de vérification des services Google"
            Handler(Looper.getMainLooper()).postDelayed({
                onDismiss()
            }, 2000)
            return@LaunchedEffect
        }
    }

    Dialog(
        onDismissRequest = {
            scope.launch {
                onDismiss()
            }
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                var streetViewPanoramaView by remember { mutableStateOf<StreetViewPanoramaView?>(null) }

                DisposableEffect(lifecycleOwner) {
                    val lifecycleObserver = object : LifecycleObserver {
                        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
                        fun onCreate() {
                            try {
                                streetViewPanoramaView?.onCreate(null)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        @OnLifecycleEvent(Lifecycle.Event.ON_START)
                        fun onStart() {
                            try {
                                streetViewPanoramaView?.onStart()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
                        fun onResume() {
                            try {
                                streetViewPanoramaView?.onResume()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                        fun onPause() {
                            try {
                                streetViewPanoramaView?.onPause()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
                        fun onStop() {
                            try {
                                streetViewPanoramaView?.onStop()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                        fun onDestroy() {
                            try {
                                streetViewPanoramaView?.onDestroy()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

                    onDispose {
                        try {
                            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
                            streetViewPanoramaView?.onDestroy()
                            streetViewPanoramaView = null
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                // Affichage du chargement
                if (isInitializing || !isPanoramaReady) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Chargement de la vue 360°...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                AndroidView(
                    factory = { ctx ->
                        try {
                            StreetViewPanoramaView(ctx).apply {
                                streetViewPanoramaView = this
                                onCreate(null)
                            }
                        } catch (e: Exception) {
                            errorMessage = "Erreur de création de la vue"
                            throw e
                        }
                    },
                    update = { view ->
                        try {
                            view.getStreetViewPanoramaAsync { panorama ->
                                try {
                                    val position = LatLng(
                                        place.latitude.toDouble(),
                                        place.longitude.toDouble()
                                    )

                                    panorama.apply {
                                        isPanningGesturesEnabled = true
                                        isZoomGesturesEnabled = true
                                        isStreetNamesEnabled = true
                                        isUserNavigationEnabled = true
                                    }

                                    // Essayer avec différents rayons de recherche
                                    var currentRadius = 50
                                    fun tryNextRadius() {
                                        if (currentRadius > 1000) {
                                            errorMessage = "Aucune vue disponible à proximité"
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                onDismiss()
                                            }, 2000)
                                            return
                                        }

                                        try {
                                            panorama.setPosition(
                                                position,
                                                currentRadius,
                                                StreetViewSource.DEFAULT
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                        currentRadius *= 2
                                    }

                                    panorama.setOnStreetViewPanoramaChangeListener { location ->
                                        if (location != null) {
                                            isPanoramaReady = true
                                            isInitializing = false
                                            errorMessage = null
                                        } else {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                tryNextRadius()
                                            }, 1000)
                                        }
                                    }

                                    // Premier essai
                                    tryNextRadius()

                                } catch (e: Exception) {
                                    errorMessage = "Erreur d'initialisation du panorama"
                                    e.printStackTrace()
                                }
                            }
                        } catch (e: Exception) {
                            errorMessage = "Erreur de chargement"
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (isPanoramaReady) 1f else 0f)
                )

                // Affichage des erreurs
                errorMessage?.let { error ->
                    Surface(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Bouton de fermeture
                IconButton(
                    onClick = {
                        scope.launch {
                            try {
                                streetViewPanoramaView?.onDestroy()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "Fermer",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}


@Composable
fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}