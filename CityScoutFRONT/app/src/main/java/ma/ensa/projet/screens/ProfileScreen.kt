package ma.ensa.projet.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ma.ensa.projet.components.AppTopBar
import ma.ensa.projet.components.BottomNavigationBar
import ma.ensa.projet.components.DrawerMenu
import ma.ensa.projet.retrofit.ApiClient
import ma.ensa.projet.retrofit.Utilisateur
import ma.ensa.projet.viewModel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    userId: Long,
    onLogout: () -> Unit
) {
    var user by remember { mutableStateOf<Utilisateur?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var updatedNom by remember { mutableStateOf("") }
    var updatedPrenom by remember { mutableStateOf("") }
    var updatedEmail by remember { mutableStateOf("") }
    var updatedUsername by remember { mutableStateOf("") }
    var updatedPays by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val isDarkTheme = themeViewModel.isDarkTheme.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    LaunchedEffect(userId) {
        try {
            user = ApiClient.getProfile(userId)
            updatedNom = user?.nom ?: ""
            updatedPrenom = user?.prenom ?: ""
            updatedEmail = user?.email ?: ""
            updatedUsername = user?.username ?: ""
            updatedPays = user?.pays ?: ""
        } catch (e: Exception) {
            Toast.makeText(navController.context, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
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
                    title = "Paramètres",
                    scope = scope,
                    drawerState = drawerState
                )
            },
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // En-tête du profil avec avatar
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isEditing) {
                        // Mode affichage
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Informations personnelles",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    IconButton(onClick = { isEditing = true }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Modifier"
                                        )
                                    }
                                }

                                Divider()

                                ProfileInfoItem("Nom", user?.nom)
                                ProfileInfoItem("Prénom", user?.prenom)
                                ProfileInfoItem("Email", user?.email)
                                ProfileInfoItem("Username", user?.username)
                                ProfileInfoItem("Pays", user?.pays)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Boutons d'action
                        FilledTonalButton(
                            onClick = { isEditing = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Modifier le profil")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        FilledTonalButton(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Déconnexion")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Supprimer le compte")
                            }
                        }
                    } else {
                        // Mode édition
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Modifier le profil",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    IconButton(onClick = { isEditing = false }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Fermer"
                                        )
                                    }
                                }

                                Divider()

                                OutlinedTextField(
                                    value = updatedNom,
                                    onValueChange = { updatedNom = it },
                                    label = { Text("Nom") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = updatedPrenom,
                                    onValueChange = { updatedPrenom = it },
                                    label = { Text("Prénom") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = updatedEmail,
                                    onValueChange = { updatedEmail = it },
                                    label = { Text("Email") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = updatedUsername,
                                    onValueChange = { updatedUsername = it },
                                    label = { Text("Username") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = updatedPays,
                                    onValueChange = { updatedPays = it },
                                    label = { Text("Pays") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { isEditing = false },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Annuler")
                                    }

                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                try {
                                                    val updatedUser = Utilisateur(
                                                        id = userId,
                                                        nom = updatedNom,
                                                        prenom = updatedPrenom,
                                                        email = updatedEmail,
                                                        username = updatedUsername,
                                                        pays = updatedPays,
                                                        photo = user?.photo
                                                    )
                                                    ApiClient.updateProfile(userId, updatedUser)
                                                    user = updatedUser
                                                    isEditing = false
                                                    Toast.makeText(
                                                        navController.context,
                                                        "Profil mis à jour",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } catch (e: Exception) {
                                                    Toast.makeText(
                                                        navController.context,
                                                        "Erreur: ${e.message}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Enregistrer")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text(
                            "Supprimer le compte",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    text = {
                        Text(
                            "Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        ApiClient.deleteAccount(userId)
                                        Toast.makeText(navController.context, "Compte supprimé", Toast.LENGTH_SHORT).show()
                                        onLogout()
                                    } catch (e: Exception) {
                                        Toast.makeText(navController.context, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Confirmer")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showDeleteDialog = false }) {
                            Text("Annuler")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(label: String, value: String?) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = RoundedCornerShape(8.dp)
        )
        .padding(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value ?: "-",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}