package ma.ensa.projet.viewModel
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ma.ensa.projet.models.Post
import ma.ensa.projet.models.PostType
import ma.ensa.projet.models.User

import ma.ensa.projet.utils.ImageUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class PostViewModel(context: Context) : ViewModel() {
    private val postApiService = RetrofitConfig.createPostApiService(context)
    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    init {
        val userId = sharedPreferences.getLong("user_id", -1L)
        if (userId != -1L) {
            fetchUserDetails(userId)
        }
    }

    private fun fetchUserDetails(userId: Long) {
        viewModelScope.launch {
            try {
                _currentUser.value = postApiService.getUserById(userId)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error fetching user", e)
            }
        }
    }

    fun createPost(content: String, type: PostType, imageBytes: ByteArray) {
        viewModelScope.launch {
            try {
                _currentUser.value?.let { user ->
                    val post = Post(
                        content = content,
                        type = type,
                        imageBytes = imageBytes,
                        utilisateur = user
                    )
                    postApiService.createPost(post)
                    fetchPosts()
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error creating post", e)
            }
        }
    }

    fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fetchedPosts = postApiService.getPosts()
                Log.d("PostViewModel", "Fetched ${fetchedPosts.size} posts")
                _posts.value = fetchedPosts
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error fetching posts", e)
                _error.value = "Failed to fetch posts: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun likePost(postId: Long, userId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val updatedPost = postApiService.likePost(postId, userId)

                // Update the posts list with the new like status
                _posts.value = _posts.value.map { post ->
                    if (post.id == postId) updatedPost else post
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error liking post", e)
                _error.value = "Failed to like post: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
