package ma.ensa.projet.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ma.ensa.projet.models.Place
import org.json.JSONObject
import org.json.JSONArray
import java.net.URL

class PlaceRepository {
    private val baseUrl = "http://192.168.11.103:5000/get-data"

    // Liste des catégories avec leurs variantes possibles
    private val categoryMap = mapOf(
        "hotel" to listOf("hotel", "hôtel", "hotels", "hôtels", "hébergement"),
        "restaur" to listOf("restaurant", "restaurants", "resto", "restos", "restauration"),
        "store" to listOf("store", "stores", "magasin", "magasins", "boutique", "boutiques", "commerce")
    )

    // Liste des pays avec leurs variantes
    private val countryMap = mapOf(
        "morocco" to listOf("morocco", "maroc", "marocco"),
        "france" to listOf("france", "la france"),
        "spain" to listOf("spain", "espagne"),
        "italy" to listOf("italy", "italie"),
        "germany" to listOf("germany", "allemagne")
    )

    suspend fun searchPlaces(query: String): List<Place> = withContext(Dispatchers.IO) {
        try {
            val (category, country) = extractCategoryAndCountry(query)

            if (category == null || country == null) {
                Log.e("PlaceRepository", "Could not extract category ($category) or country ($country) from the query: $query")
                return@withContext emptyList()
            }

            val sqlQuery = """
                SELECT * FROM places 
                WHERE category = '$category' 
                AND country = '$country'
            """.trimIndent()

            val connection = URL(baseUrl).openConnection()
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonInput = JSONObject().apply {
                put("query", sqlQuery)
            }

            connection.outputStream.use {
                it.write(jsonInput.toString().toByteArray())
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            return@withContext parsePlacesResponse(response)
        } catch (e: Exception) {
            Log.e("PlaceRepository", "Error fetching places", e)
            return@withContext emptyList()
        }
    }

    private fun extractCategoryAndCountry(query: String): Pair<String?, String?> {
        val lowerQuery = query.toLowerCase()

        // Recherche de la catégorie en utilisant la map
        val category = categoryMap.entries.find { (_, variants) ->
            variants.any { variant -> lowerQuery.contains(variant) }
        }?.key

        // Recherche du pays en utilisant la map
        val country = countryMap.entries.find { (_, variants) ->
            variants.any { variant -> lowerQuery.contains(variant) }
        }?.key

        Log.d("PlaceRepository", "Extracted category: $category, country: $country from query: $query")
        return Pair(category, country)
    }

    private fun parsePlacesResponse(response: String): List<Place> {
        return try {
            JSONArray(response).let { jsonArray ->
                List(jsonArray.length()) { index ->
                    val obj = jsonArray.getJSONObject(index)
                    Place(
                        id = obj.getLong("id"),
                        place_name = obj.getString("name"),
                        address = obj.getString("address"),
                        category = obj.getString("category"),
                        country = obj.getString("country"),
                        rating = obj.getDouble("rating").toInt(),
                        latitude = obj.getDouble("latitude"),
                        longitude = obj.getDouble("longitude"),
                        photo_url = obj.getString("photo_url"),
                        flag_url = obj.getString("flag_url"),
                        types = obj.getString("types"),
                        user_ratings_total = obj.getInt("user_ratings_total")
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("PlaceRepository", "Error parsing response", e)
            emptyList()
        }
    }
}
