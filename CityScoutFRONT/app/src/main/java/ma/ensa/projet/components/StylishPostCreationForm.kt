package ma.ensa.projet.components

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import ma.ensa.projet.viewModel.PostViewModel
import ma.ensa.projet.viewModel.PostViewModelFactory
import ma.ensa.projet.viewModel.ThemeViewModel
import ma.ensa.projet.models.*
import ma.ensa.projet.viewModel.*
import ma.ensa.projet.components.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StylishPostCreationForm(
    content: String,
    onContentChanged: (String) -> Unit,
    selectedType: PostType?,
    onTypeSelected: (PostType) -> Unit,
    selectedImageBytes: ByteArray?,
    onImageSelected: (ByteArray?) -> Unit,
    onImagePick: () -> Unit,
    onCreatePost: (String, PostType, ByteArray?) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Map for English translations and visual properties for each post type
    val postTypeProperties = mapOf(
        PostType.BON_PLAN to TypeProperties(
            label = "Deal",
            icon = Icons.Rounded.LocalOffer,
            color = Color(0xFF4CAF50)
        ),
        PostType.EVENEMENT to TypeProperties(
            label = "Event",
            icon = Icons.Rounded.Event,
            color = Color(0xFF2196F3)
        ),
        PostType.CONSEIL to TypeProperties(
            label = "Tip",
            icon = Icons.Rounded.Lightbulb,
            color = Color(0xFFFFA000)
        ),
        PostType.QUESTION to TypeProperties(
            label = "Question",
            icon = Icons.Rounded.Help,
            color = Color(0xFFE91E63)
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpanded) 8.dp else 4.dp,
            focusedElevation = 12.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Create,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Create a Post",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = content,
                        onValueChange = {
                            onContentChanged(it)
                            if (!isExpanded) isExpanded = true
                        },
                        placeholder = {
                            Text(
                                "Share something with the community...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        var expanded by remember { mutableStateOf(false) }

                        // Type selector button
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = selectedType?.let { postTypeProperties[it]?.color?.copy(alpha = 0.15f) }
                                        ?: MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = selectedType?.let { postTypeProperties[it]?.color }
                                        ?: MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                selectedType?.let { type ->
                                    postTypeProperties[type]?.let { props ->
                                        Icon(
                                            imageVector = props.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            props.label,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } ?: run {
                                    Icon(
                                        imageVector = Icons.Rounded.Label,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Select Type",
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface)
                                    .width(IntrinsicSize.Min)
                            ) {
                                postTypeProperties.forEach { (type, props) ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = props.icon,
                                                    contentDescription = null,
                                                    tint = props.color,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Text(
                                                    props.label,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        },
                                        onClick = {
                                            onTypeSelected(type)
                                            expanded = false
                                        },
                                        colors = MenuDefaults.itemColors(
                                            textColor = props.color
                                        )
                                    )
                                }
                            }
                        }

                        // Image picker button
                        Button(
                            onClick = onImagePick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddPhotoAlternate,
                                contentDescription = "Add image",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Add Photo",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Image preview
                    if (selectedImageBytes != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Image(
                                bitmap = remember(selectedImageBytes) {
                                    BitmapFactory.decodeByteArray(
                                        selectedImageBytes,
                                        0,
                                        selectedImageBytes.size
                                    ).asImageBitmap()
                                },
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            IconButton(
                                onClick = { onImageSelected(null) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Remove image",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    // Post button
                    Button(
                        onClick = {
                            selectedType?.let { type ->
                                onCreatePost(content, type, selectedImageBytes)
                                isExpanded = false
                            }
                        },
                        enabled = content.isNotBlank() && selectedType != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = selectedType?.let { postTypeProperties[it]?.color }
                                ?: MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            "Share Post",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private data class TypeProperties(
    val label: String,
    val icon: ImageVector,
    val color: Color
)