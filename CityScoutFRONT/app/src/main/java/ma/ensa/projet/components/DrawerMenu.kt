package ma.ensa.projet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ma.ensa.projet.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerMenu(
    navController: NavController,
    isDarkTheme: State<Boolean>,
    drawerState: DrawerState, // Recevez DrawerState ici
    onToggleTheme: () -> Unit, // Nouvelle fonction pour basculer le thème
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    val drawerItems = listOf(
        DrawerItem("Home", Icons.Default.Home, Route.Home.route),
        DrawerItem("Favorites", Icons.Default.Favorite, Route.Favoris.route),
        DrawerItem("Profile", Icons.Default.Person, Route.Profile.route),
        DrawerItem("AboutScreen", Icons.Default.Settings, Route.AboutScreen.route)
    )

    // Add a logout item
    val logoutItem = DrawerItem("Logout", Icons.Default.ExitToApp, Route.Login.route)

    ModalNavigationDrawer(
        drawerState = drawerState, // Utilisez le même DrawerState
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CityScout",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                // Drawer menu items
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        selected = false,
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch {
                                drawerState.close() // Fermez le drawer après la navigation
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                // Add Logout item
                NavigationDrawerItem(
                    label = { Text(logoutItem.title) },
                    selected = false,
                    icon = { Icon(logoutItem.icon, contentDescription = logoutItem.title) },
                    onClick = {
                        // Navigate to the login screen
                        navController.navigate(logoutItem.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        scope.launch {
                            drawerState.close() // Fermez le drawer après la navigation
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Dark mode toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mode Sombre")
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isDarkTheme.value,
                        onCheckedChange = { onToggleTheme() } // Appelle la méthode pour changer le thème
                    )
                }
            }
        },
        content = content
    )
}

// Helper class for drawer items remains the same
data class DrawerItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)
