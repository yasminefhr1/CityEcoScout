package ma.ensa.projet.retrofit

import ma.ensa.projet.models.Post
import ma.ensa.projet.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path



interface PostApiService {
    @GET("api/posts")
    suspend fun getPosts(): List<Post>

    @POST("api/posts")
    suspend fun createPost(@Body post: Post): Post

    @POST("api/posts/{postId}/like/{userId}")  // Mise à jour du chemin
    suspend fun likePost(
        @Path("postId") postId: Long,
        @Path("userId") userId: Long
    ): Post

    @GET("user/{id}")
    suspend fun getUserById(@Path("id") id: Long): User
}
