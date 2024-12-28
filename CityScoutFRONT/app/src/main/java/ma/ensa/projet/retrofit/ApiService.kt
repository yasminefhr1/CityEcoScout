package ma.ensa.projet.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    @GET("user/{id}")
    suspend fun getProfile(@Path("id") id: Long): Utilisateur

    @PUT("user/{id}")
    suspend fun updateProfile(
        @Path("id") id: Long,
        @Body user: Utilisateur
    ): Utilisateur

    @DELETE("user/{id}")
    suspend fun deleteAccount(@Path("id") id: Long)
}



