package ma.ensa.projet.retrofit

import android.util.Log
import ma.ensa.projet.models.Place
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query

interface PlaceApi {
    @GET("api/places")
    suspend fun getPlaces(): List<Place>

    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        suspend fun getPlaceById(id: Long): Place? {
            return try {
                retrofit.create(PlaceApiService::class.java).getPlaceById(id)
            } catch (e: Exception) {
                Log.e("PlaceApi", "Error getting place by id", e)
                null
            }
        }
    }
    @GET("places/search")
    suspend fun searchPlaces(@Query("query") query: String): List<Place>
}

interface PlaceApiService {
    @GET("places/{id}")
    suspend fun getPlaceById(@Path("id") id: Long): Place
}