package ma.ensa.projet

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import ma.ensa.projet.navigation.AppNavigation
import ma.ensa.projet.ui.theme.CityScoutTheme
import ma.ensa.projet.viewModel.ThemeViewModel

class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Vérifiez et demandez les permissions nécessaires
        checkLocationPermission()

        setContent {
            val isDarkTheme = themeViewModel.isDarkTheme.collectAsState() // Observer l'état du thème
            val navController = rememberNavController()

            CityScoutTheme(darkTheme = isDarkTheme.value) {
                // Passez languageViewModel en plus de themeViewModel
                AppNavigation(
                    navController = navController,
                    themeViewModel = themeViewModel
                )
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }
}
