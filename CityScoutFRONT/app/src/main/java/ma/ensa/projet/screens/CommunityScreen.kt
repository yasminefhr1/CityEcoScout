package ma.ensa.projet.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ma.ensa.projet.components.*
import ma.ensa.projet.models.Post
import ma.ensa.projet.models.PostType
import ma.ensa.projet.ui.theme.CityScoutTheme
import ma.ensa.projet.viewModel.*
import java.io.ByteArrayOutputStream
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import ma.ensa.projet.viewModel.PostViewModel
import ma.ensa.projet.viewModel.PostViewModelFactory
import ma.ensa.projet.viewModel.ThemeViewModel
import ma.ensa.projet.models.*
import ma.ensa.projet.viewModel.*
import ma.ensa.projet.components.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollableFilterBar(
    selectedFilter: PostType?,
    onFilterSelected: (PostType?) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                selected = selectedFilter == null,
                onClick = { onFilterSelected(null) },
                label = { Text("All", fontWeight = FontWeight.Medium) },
                shape = RoundedCornerShape(12.dp),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    selectedBorderColor = MaterialTheme.colorScheme.primary
                ),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )

            val types = mapOf(
                PostType.BON_PLAN to Triple("Deals", Icons.Rounded.Star, Color(0xFF4CAF50)),
                PostType.EVENEMENT to Triple("Events", Icons.Rounded.Event, Color(0xFF2196F3)),
                PostType.CONSEIL to Triple("Tips", Icons.Rounded.Lightbulb, Color(0xFFFFA000))
            )

            types.forEach { (type, data) ->
                val (label, icon, color) = data
                FilterChip(
                    selected = selectedFilter == type,
                    onClick = { onFilterSelected(type) },
                    label = {
                        Text(
                            text = label,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = if (selectedFilter == type) Color.White else color
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = color.copy(alpha = 0.5f),
                        selectedBorderColor = color
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = color,
                        selectedLabelColor = Color.White,
                        containerColor = color.copy(alpha = 0.1f),
                        labelColor = color
                    )
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTypeSelector(
    selected: PostType?,
    onTypeSelected: (PostType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected?.let {
                when(it) {
                    PostType.BON_PLAN -> "Deals"
                    PostType.EVENEMENT -> "Events"
                    PostType.CONSEIL -> "Tips"
                    PostType.QUESTION -> TODO()
                }
            } ?: "",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            placeholder = { Text("Type") }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val types = mapOf(
                PostType.BON_PLAN to "Deals",
                PostType.EVENEMENT to "Events",
                PostType.CONSEIL to "Tips"
            )

            types.forEach { (type, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = when (type) {
                                PostType.BON_PLAN -> Icons.Rounded.Star
                                PostType.EVENEMENT -> Icons.Rounded.Event
                                PostType.CONSEIL -> Icons.Rounded.Lightbulb
                                PostType.QUESTION -> TODO()
                            },
                            contentDescription = null,
                            tint = when (type) {
                                PostType.BON_PLAN -> Color(0xFF4CAF50)
                                PostType.EVENEMENT -> Color(0xFF2196F3)
                                PostType.CONSEIL -> Color(0xFFFFA000)
                                PostType.QUESTION -> TODO()
                            }
                        )
                    }
                )
            }
        }
    }
}

fun uriToByteArray(context: Context, uri: Uri): ByteArray {
    return context.contentResolver.openInputStream(uri)?.use { inputStream ->
        val bitmap = BitmapFactory.decodeStream(inputStream)
        ByteArrayOutputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.toByteArray()
        }
    } ?: throw IllegalStateException("Could not open input stream")
}


@Composable
fun CommunityScreen(
    navController: NavHostController,
    userId: Long,
    themeViewModel: ThemeViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current
    val viewModel: PostViewModel = viewModel(factory = PostViewModelFactory(context))
    val isDarkTheme = themeViewModel.isDarkTheme.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    var content by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<PostType?>(null) }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var selectedFilter by remember { mutableStateOf<PostType?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                selectedImageBytes = uriToByteArray(context, it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val filteredPosts = remember(posts, selectedFilter) {
        if (selectedFilter == null) posts else posts.filter { it.type == selectedFilter }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchPosts()
    }

    DrawerMenu(
        navController = navController,
        isDarkTheme = isDarkTheme,
        drawerState = drawerState,
        onToggleTheme = { themeViewModel.toggleTheme() }
    ) {Scaffold(
        topBar = {
            AppTopBar(
                title = "Network",
                scope = scope,
                drawerState = drawerState
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { padding ->
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Post Creation Card
            item {
                StylishPostCreationForm(
                    content = content,
                    onContentChanged = { content = it },
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it },
                    selectedImageBytes = selectedImageBytes,
                    onImageSelected = { selectedImageBytes = it },
                    onImagePick = { imagePicker.launch("image/*") },
                    onCreatePost = { newContent, type, imageBytes ->
                        if (imageBytes != null) {
                            viewModel.createPost(newContent, type, imageBytes)
                        }
                        // Reset form state
                        content = ""
                        selectedImageBytes = null
                        selectedType = null
                    }
                )
            }

            // Filters
            item {
                ScrollableFilterBar(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
            }

            // Loading indicator
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // Posts List
            items(filteredPosts, key = { it.id ?: 0 }) { post ->
                PostCard(post, userId, onLikeClick = { viewModel.likePost(it.id!!, userId) })
            }
        }
    }
    }
}
