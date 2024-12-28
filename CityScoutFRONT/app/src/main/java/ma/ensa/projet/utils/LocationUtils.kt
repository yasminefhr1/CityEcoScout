package ma.ensa.projet.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import ma.ensa.projet.models.LocalBusiness
import ma.ensa.projet.data.SampleData
import java.util.Locale

class LocationUtils(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationUpdates(onLocationUpdated: (latitude: Double, longitude: Double) -> Unit) {
        try {
            if (!hasLocationPermission()) {
                Log.e("LocationUtils", "Permission de localisation non accordée.")
                return
            }

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000L // Intervalle en millisecondes
            ).setMinUpdateIntervalMillis(500L) // Intervalle minimum
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        Log.d("LocationUtils", "Localisation obtenue: ${location.latitude}, ${location.longitude}")
                        onLocationUpdated(location.latitude, location.longitude)
                    } ?: Log.e("LocationUtils", "Localisation introuvable")
                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                    if (!locationAvailability.isLocationAvailable) {
                        Log.w("LocationUtils", "La localisation n'est pas disponible.")
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        } catch (e: SecurityException) {
            Log.e("LocationUtils", "Erreur de permissions : ${e.message}", e)
        } catch (e: Exception) {
            Log.e("LocationUtils", "Erreur lors de la demande de localisation : ${e.message}", e)
        }
    }

    fun reverseGeocode(latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.isNullOrEmpty()) {
                Log.w("LocationUtils", "Aucune adresse trouvée pour la localisation: $latitude, $longitude")
                "Adresse non disponible"
            } else {
                addresses[0].getAddressLine(0)
            }
        } catch (e: Exception) {
            Log.e("LocationUtils", "Erreur de géocodage : ${e.message}", e)
            "Erreur lors de la récupération de l'adresse"
        }
    }

    fun getNearbyBusinesses(latitude: Double, longitude: Double, radius: Double = 5.0): List<LocalBusiness> {
        return SampleData.localBusinesses.filter { business ->
            val distance = calculateDistance(
                latitude, longitude,
                business.latitude, business.longitude
            )
            distance <= radius
        }.also {
            Log.d("LocationUtils", "${it.size} entreprises trouvées à proximité.")
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // Rayon de la Terre en km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }
}
