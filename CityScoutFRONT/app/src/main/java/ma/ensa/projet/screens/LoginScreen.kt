package ma.ensa.projet.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ma.ensa.projet.R
import ma.ensa.projet.navigation.Route
import ma.ensa.projet.retrofit.AuthService

@Composable
fun LoginScreen(
    navController: NavHostController,
    onLoginSuccess: (Long) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

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
            .padding(16.dp), // Ajout du padding général autour du formulaire
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.bio_icon),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Crop
        )

        // Header
        Text(
            "Login",
            style = TextStyle(
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Username input
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            isError = errorMessage.contains("username", ignoreCase = true),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            enabled = !isLoading,
            shape = MaterialTheme.shapes.medium
        )

        // Password input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = errorMessage.contains("password", ignoreCase = true),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            enabled = !isLoading,
            shape = MaterialTheme.shapes.medium
        )

        // Login Button
        Button(
            onClick = {
                isLoading = true
                errorMessage = ""
                AuthService.performLogin(
                    username = username,
                    password = password,
                    context = context,
                    onSuccess = { userId ->
                        isLoading = false
                        onLoginSuccess(userId)
                    },
                    onError = { error ->
                        isLoading = false
                        errorMessage = error
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 16.dp),
            enabled = !isLoading && username.isNotEmpty() && password.isNotEmpty(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6200EA), // Vibrant Purple
                contentColor = Color.White
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Login", style = TextStyle(fontWeight = FontWeight.Bold))
            }
        }

        // Error message display
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Signup redirection
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = { navController.navigate(Route.Signup.route) },
            enabled = !isLoading
        ) {
            Text("Don't have an account? Sign up", style = TextStyle(fontWeight = FontWeight.Bold))
        }
    }
}
