package ma.ensa.projet.retrofit

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ma.ensa.projet.models.Place
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://192.168.11.103:8080/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    suspend fun getProfile(id: Long): Utilisateur {
        return apiService.getProfile(id)
    }

    suspend fun updateProfile(id: Long, user: Utilisateur): Utilisateur {
        return apiService.updateProfile(id, user)
    }

    suspend fun deleteAccount(id: Long) {
        apiService.deleteAccount(id)
    }
}