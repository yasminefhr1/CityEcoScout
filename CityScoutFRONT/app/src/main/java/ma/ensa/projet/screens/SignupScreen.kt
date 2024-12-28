package ma.ensa.projet.screens

import android.graphics.Bitmap
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ma.ensa.projet.navigation.Route
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext

@Composable
fun SignupScreen(navController: NavHostController, onSignupSuccess: () -> Unit) {
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var pays by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var photoBase64 by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            photoUri = uri
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            photoBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6200EA), // Vibrant Purple
                        Color(0xFF03DAC6)  // Teal
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp), // Padding around the whole form
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            "Sign Up",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp) // Margin below the header
        )

        // Profile picture section
        if (photoUri != null) {
            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(photoUri!!))
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp), // Margin below the profile picture
                contentScale = ContentScale.Crop
            )
        } else {
            TextButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
            ) {
                Text("Choose Profile Picture")
            }
        }

        // Name fields
        OutlinedTextField(
            value = nom,
            onValueChange = { nom = it },
            label = { Text("Nom") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp) // Margin between input fields
        )

        OutlinedTextField(
            value = prenom,
            onValueChange = { prenom = it },
            label = { Text("Prénom") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = pays,
            onValueChange = { pays = it },
            label = { Text("Pays") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp) // Bottom margin after last field
        )

        // Sign up button
        Button(
            onClick = {
                errorMessage = ""
                if (nom.isEmpty() || prenom.isEmpty() || pays.isEmpty() || email.isEmpty() || username.isEmpty()) {
                    errorMessage = "Required fields must be filled."
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    errorMessage = "Invalid email format."
                } else if (password != confirmPassword) {
                    errorMessage = "Passwords do not match."
                } else {
                    isLoading = true
                    performSignup(
                        nom = nom,
                        prenom = prenom,
                        pays = pays,
                        email = email,
                        username = username,
                        password = password,
                        confirmPassword = confirmPassword,
                        photo = photoBase64,
                        onSuccess = {
                            isLoading = false
                            navController.navigate(Route.Login.route)
                        },
                        onError = { error ->
                            isLoading = false
                            errorMessage = error
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp), // Bottom margin before the button
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Sign Up")
            }
        }

        // Error message
        if (errorMessage.isNotEmpty()) {
            Text(
                errorMessage,
                color = Color.Red,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
        }

        // Sign In redirect
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate(Route.Login.route) }) {
            Text("Already have an account? Login")
        }
    }
}


private fun performSignup(
    nom: String,
    prenom: String,
    pays: String,
    email: String,
    username: String,
    password: String,
    confirmPassword: String,
    photo: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val client = OkHttpClient()
    val jsonBody = JSONObject().apply {
        put("nom", nom)
        put("prenom", prenom)
        put("pays", pays)
        put("email", email)
        put("username", username)
        put("password", password)
        put("confirmPassword", confirmPassword)
        put("photo", photo)
    }

    val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url("http://192.168.11.103:8080/auth/signup") // Ensure this URL is correct
        .post(requestBody)
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                CoroutineScope(Dispatchers.Main).launch {
                    onError("Network error: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    CoroutineScope(Dispatchers.Main).launch {
                        onSuccess()
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        onError("Signup failed: ${response.message}")
                    }
                }
            }
        })
    }
}