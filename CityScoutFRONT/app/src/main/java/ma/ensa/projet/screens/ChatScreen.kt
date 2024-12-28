package ma.ensa.projet.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ma.ensa.projet.R
import ma.ensa.projet.components.AppTopBar
import ma.ensa.projet.components.BottomNavigationBar
import ma.ensa.projet.components.DrawerMenu
import ma.ensa.projet.models.ChatMessage
import ma.ensa.projet.viewModel.ChatViewModel
import ma.ensa.projet.viewModel.ThemeViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    chatViewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val isDarkTheme = themeViewModel.isDarkTheme.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val messages by chatViewModel.messages.collectAsState()
    var messageText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isTyping by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val listState = rememberLazyListState()

    // Example messages to show before chat starts
    val exampleMessages = remember {
        listOf(
            "Hi! What are the most sustainable travel destinations in the world? 🌆",
            "How do I reduce food waste in my daily life? 🍽️",
            "How can I make my travel more sustainable? ",
            "Give me a sustainble Hotel in France? 🎉"
        )
    }

    val dotCount by rememberInfiniteTransition().animateValue(
        initialValue = 1,
        targetValue = 3,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            selectedBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it))
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
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
                    title = "CityScout Chat",
                    scope = scope,
                    drawerState = drawerState
                )
            },
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        ) { padding ->
            Box(
                Modifier.fillMaxSize()
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    if (messages.isEmpty()) {
                        WelcomeSection(exampleMessages)
                    }

                    ChatContent(
                        messages = messages,
                        isDarkTheme = isDarkTheme.value,
                        listState = listState,
                        isTyping = isTyping,
                        dotCount = dotCount,
                        modifier = Modifier.weight(1f)
                    )

                    ChatInputBar(
                        messageText = messageText,
                        onMessageChange = {
                            messageText = it
                            isTyping = true
                            scope.launch {
                                delay(300)
                                isTyping = false
                            }
                        },
                        selectedBitmap = selectedBitmap,
                        onSendMessage = {
                            if (messageText.isNotBlank() || selectedImageUri != null) {
                                chatViewModel.sendMessage(
                                    message = messageText,
                                    imageUri = selectedImageUri?.toString() ?: "",
                                    bitmap = selectedBitmap
                                )
                                messageText = ""
                                selectedImageUri = null
                                selectedBitmap = null
                            }
                        },
                        onImageSelect = {
                            imagePicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeSection(exampleMessages: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Welcome to CityScout Chat!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Try asking about:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                exampleMessages.forEach { message ->
                    SuggestionChip(
                        onClick = { /* Handle suggestion click */ },
                        label = { Text(message) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage, isDarkTheme: Boolean) {
    val messageColor = if (message.isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Box(
            Modifier
                .align(if (message.isUser) Alignment.End else Alignment.Start)
                .widthIn(max = 300.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = messageColor),
                shape = RoundedCornerShape(
                    topStart = if (message.isUser) 16.dp else 4.dp,
                    topEnd = if (message.isUser) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            ) {
                Column(Modifier.padding(12.dp)) {
                    message.bitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Sent image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(
                        text = message.content,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatInputBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    selectedBitmap: Bitmap?,
    onSendMessage: () -> Unit,
    onImageSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            selectedBitmap?.let { bitmap ->
                Card(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .height(120.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Selected image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onImageSelect) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Add image",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }


                TextField(
                    value = messageText,
                    onValueChange = onMessageChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type your message...") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                IconButton(
                    onClick = onSendMessage,
                    enabled = messageText.isNotBlank() || selectedBitmap != null
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (messageText.isNotBlank() || selectedBitmap != null)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatContent(
    messages: List<ChatMessage>,
    isDarkTheme: Boolean,
    listState: LazyListState,
    isTyping: Boolean,
    dotCount: Int,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(messages) { message ->
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    initialOffsetX = { if (message.isUser) it else -it }
                ) + fadeIn() + expandVertically(),
                exit = slideOutHorizontally() + fadeOut() + shrinkVertically()
            ) {
                ChatMessageItem(message = message, isDarkTheme = isDarkTheme)
            }
        }

        if (isTyping) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        repeat(3) { index ->
                            Box(
                                Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index < dotCount)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}