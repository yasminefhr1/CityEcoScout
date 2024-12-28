import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import ma.ensa.projet.retrofit.ByteArrayDeserializer
import ma.ensa.projet.retrofit.PostApiService
import java.util.concurrent.TimeUnit

object RetrofitConfig {
    private const val BASE_URL = "http://192.168.11.107:8080/"
    private const val TIMEOUT = 60L // seconds

    fun createPostApiService(context: Context): PostApiService {
        val token = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .getString("auth_token", "")

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(ByteArray::class.java, ByteArrayDeserializer())
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(PostApiService::class.java)
    }
}