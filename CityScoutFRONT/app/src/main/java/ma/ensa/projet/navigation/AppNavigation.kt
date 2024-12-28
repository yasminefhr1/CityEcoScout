package ma.ensa.projet.navigation

import ma.ensa.projet.screens.*
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ma.ensa.projet.viewModel.ThemeViewModel
import ma.ensa.projet.utils.LocationUtils
import ma.ensa.projet.viewModel.PlaceViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    themeViewModel: ThemeViewModel
) {
    val context = LocalContext.current
    val placeViewModel: PlaceViewModel = viewModel()
    val sharedPreferences = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }

    NavHost(
        navController = navController,
        startDestination = Route.Login.route  // Login est toujours le point de départ
    ) {
        // Écran de connexion
        composable(Route.Login.route) {
            LoginScreen(
                navController = navController,
                onLoginSuccess = { userId ->
                    // Sauvegarder l'ID de l'utilisateur et l'état de connexion
                    with(sharedPreferences.edit()) {
                        putLong("user_id", userId)
                        putBoolean("is_logged_in", true)
                        apply()
                    }
                    // Naviguer vers Home
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Écran d'inscription
        composable(Route.Signup.route) {
            SignupScreen(
                navController = navController,
                onSignupSuccess = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Signup.route) { inclusive = true }
                    }
                }
            )
        }

        // Écran principal (Home)
        composable(Route.Home.route) {
            HomeScreen(
                navController = navController,
                themeViewModel = themeViewModel,
                placeViewModel = placeViewModel
            )
        }

        composable(Route.Favoris.route) {
            val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getLong("user_id", -1L)

            if (userId != -1L) {
                FavorisScreen(
                    navController = navController,
                    themeViewModel = themeViewModel,
                    userId = userId
                )
            } else {
                // Si l'utilisateur n'est pas connecté, rediriger vers l'écran de connexion
                LaunchedEffect(Unit) {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Favoris.route) { inclusive = true }
                    }
                }
            }
        }


        composable(Route.Chat.route) {
            ChatScreen(
                navController = navController,
                themeViewModel = themeViewModel
            )
        }

        // Écran de profil avec userId
        composable(Route.Profile.route) {
            val userId = sharedPreferences.getLong("user_id", -1L)
            if (userId != -1L) {
                ProfileScreen(
                    navController = navController,
                    themeViewModel = themeViewModel,
                    userId = userId,
                    onLogout = {
                        // Effacer les données utilisateur lors de la déconnexion
                        with(sharedPreferences.edit()) {
                            clear()
                            apply()
                        }
                        // Retourner à l'écran de connexion
                        navController.navigate(Route.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Route.AboutScreen.route) {
            AboutScreen(
                navController = navController,
                themeViewModel = themeViewModel

            )
        }

        composable(Route.Map.route) {
            val locationUtils = remember { LocationUtils(context) }
            MapScreen(
                placeViewModel = placeViewModel,
                themeViewModel = themeViewModel,
                navController = navController
            )
        }

        composable(Route.Search.route) {
            SearchScreen(
                navController = navController,
                themeViewModel = themeViewModel,
                placeViewModel = placeViewModel
            )
        }

        composable("detail") {
            PlaceDetailScreen(
                placeViewModel = placeViewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable(Route.Community.route) {
            val userId = sharedPreferences.getLong("user_id", -1L)
            if (userId != -1L) {
                CommunityScreen(
                    navController = navController,
                    userId = userId,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}


// Fonctions utilitaires pour gérer l'état d'authentification
fun isUserLoggedIn(sharedPreferences: SharedPreferences): Boolean {
    return sharedPreferences.getBoolean("is_logged_in", false)
}

fun saveUserLoggedInState(sharedPreferences: SharedPreferences, isLoggedIn: Boolean) {
    sharedPreferences.edit().putBoolean("is_logged_in", isLoggedIn).apply()
}