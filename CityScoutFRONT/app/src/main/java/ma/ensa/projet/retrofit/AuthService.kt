package ma.ensa.projet.retrofit

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object AuthService {
    private val client = OkHttpClient()
    private const val BASE_URL = "http://192.168.11.107:8080"

    fun performLogin(
        username: String,
        password: String,
        context: Context,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        val jsonBody = JSONObject().apply {
            put("username", username)
            put("password", password)
        }

        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/auth/login")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                CoroutineScope(Dispatchers.Main).launch {
                    Log.e("LoginError", "Network error: ${e.message}")
                    onError("Network error: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("LoginResponse", "Raw response: $responseBody") // Debug log

                CoroutineScope(Dispatchers.Main).launch {
                    if (response.isSuccessful) {
                        try {
                            val jsonResponse = JSONObject(responseBody!!)

                            // More flexible parsing for userId
                            val userId = try {
                                jsonResponse.getLong("userId")
                            } catch (e: Exception) {
                                jsonResponse.getString("userId").toLong()
                            }

                            val token = jsonResponse.getString("token")

                            // Debug logging
                            Log.d("LoginResponse", "Parsed token: $token")
                            Log.d("LoginResponse", "Parsed userId: $userId")

                            saveAuthToken(context, token)
                            saveUserId(context, userId)
                            onSuccess(userId)
                        } catch (e: Exception) {
                            Log.e("LoginError", "Parse error: ${e.message}")
                            Log.e("LoginError", "Response body was: $responseBody")
                            e.printStackTrace()
                            onError("Invalid response format: ${e.message}")
                        }
                    } else {
                        Log.e("LoginError", "Login failed with code: ${response.code}")
                        Log.e("LoginError", "Error body: $responseBody")
                        onError("Login failed: ${response.message}")
                    }
                }
            }
        })
    }

    fun performGoogleLogin(
        idToken: String,
        context: Context,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        val jsonBody = JSONObject().apply {
            put("idToken", idToken)
        }

        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/login/oauth2/code/google")  // Updated endpoint to match Spring Boot
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("GoogleAuth", "Network error: ${e.message}")
                CoroutineScope(Dispatchers.Main).launch {
                    onError("Network error: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("GoogleAuth", "Response: $responseBody")

                CoroutineScope(Dispatchers.Main).launch {
                    if (response.isSuccessful) {
                        try {
                            val jsonResponse = JSONObject(responseBody!!)
                            val userId = jsonResponse.getLong("userId")
                            val token = jsonResponse.getString("token")

                            saveAuthToken(context, token)
                            saveUserId(context, userId)
                            onSuccess(userId)
                        } catch (e: Exception) {
                            Log.e("GoogleAuth", "Parse error: ${e.message}")
                            onError("Invalid response format: ${e.message}")
                        }
                    } else {
                        Log.e("GoogleAuth", "Auth failed: ${response.code}")
                        onError("Google login failed: ${response.message}")
                    }
                }
            }
        })
    }

    private fun saveAuthToken(context: Context, token: String) {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("auth_token", token)
            .apply()
    }

    private fun saveUserId(context: Context, userId: Long) {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .edit()
            .putLong("user_id", userId)
            .apply()
    }

    fun getAuthToken(context: Context): String? {
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .getString("auth_token", null)
    }

    fun getUserId(context: Context): Long {
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .getLong("user_id", -1)
    }

    fun clearAuthData(context: Context) {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}