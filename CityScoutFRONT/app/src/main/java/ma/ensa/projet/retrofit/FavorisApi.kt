package ma.ensa.projet.retrofit

import ma.ensa.projet.models.FavoriteRequest
import ma.ensa.projet.models.Place
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FavorisApi {
    @POST("favorites/toggle")
    suspend fun toggleFavorite(@Body request: FavoriteRequest): Response<Unit>

    @GET("favorites/status")
    suspend fun getFavoriteStatus(@Query("userId") userId: Long, @Query("placeId") placeId: Long): Response<Boolean>

    @POST("favorites/add")
    suspend fun addFavorite(@Body request: FavoriteRequest): Response<Unit>

    @POST("favorites/remove")
    suspend fun removeFavorite(@Body request: FavoriteRequest): Response<Unit>

    @GET("favorites/favorites/user")
    suspend fun getUserFavorites(@Query("userId") userId: Long): Response<List<Place>>

    companion object {
        fun create(): FavorisApi {
            return Retrofit.Builder()
                .baseUrl("http://192.168.11.107:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FavorisApi::class.java)
        }
    }





}


// Classe de réponse pour vérifier si un favori existe
data class FavoriteStatus(val isFavorite: Boolean)

data class FavoriteResponse(
    val id: Long,
    val business: Place, // Assurez-vous que le modèle Business est bien défini
    val userId: Long
)