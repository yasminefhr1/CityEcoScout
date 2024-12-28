package ma.ensa.projet.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ma.ensa.projet.navigation.Route

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Community,
        NavigationItem.Map,       // Nouvelle option Carte
        NavigationItem.Search  ,   // Nouvelle option Recherche
        NavigationItem.Chat
    )


    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // S'assurer que l'on nettoie la pile des écrans précédents si nécessaire
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                            restoreState = true
                        }

                    }
                }
            )

        }
    }
}


sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : NavigationItem(Route.Home.route, Icons.Filled.Home, "Home")
    object Community : NavigationItem(Route.Community.route, Icons.Filled.PostAdd, "Network") // Icône remplacée par Favorite

    object Chat : NavigationItem(Route.Chat.route, Icons.Filled.Chat, "Chat")
    object Map : NavigationItem(Route.Map.route, Icons.Filled.Map, "Map") // Nouvelle option Carte
    object Search : NavigationItem(Route.Search.route, Icons.Filled.Search, "Search") // Nouvelle option Recherche
}
