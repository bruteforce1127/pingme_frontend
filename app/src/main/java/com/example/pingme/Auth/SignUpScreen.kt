package com.example.pingme.Auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pingme.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Define your user model
data class signUpData(
    var username: String,
    var fullname: String,
    var password: String,
    var mail: String
)

// Define your API interface
interface UserApiService {
    @POST("/register")
    suspend fun registerUser(@Body user: signUpData): signUpData
}

// Create a Retrofit instance
object RetrofitSignUpClient {
    private const val BASE_URL = "http://172.22.78.106:8080"

    val apiService: UserApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApiService::class.java)
    }
}

@Composable
fun SignUpScreen(
    goToLoginScreen : ()->Unit
) {

    // Input fields state
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") } // Added fullName if needed
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Obtain the context for Toast messages
    val context = LocalContext.current

    // Create a coroutine scope for launching suspend functions
    val coroutineScope = rememberCoroutineScope()

    var isAnimationComplete by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isAnimationComplete = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background same as LoginScreen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(if (isAnimationComplete) 1f else 1.1f)
                .alpha(if (isAnimationComplete) 1f else 0.6f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2B32B2),
                            Color(0xFF1488CC),
                            Color(0xFF2B32B2)
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {}
            (1..6).forEach { index ->
                val offsetX = when(index % 3) {
                    0 -> -100.dp
                    1 -> 150.dp
                    else -> 300.dp
                }
                val offsetY = when(index % 2) {
                    0 -> (100 + index * 100).dp
                    else -> (200 + index * 80).dp
                }
                val size = (100 + (index % 3) * 50).dp
                Box(
                    modifier = Modifier
                        .size(size)
                        .offset(offsetX, offsetY)
                        .alpha(0.4f)
                        .blur(20.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4E92F7),
                                    Color(0xFF4E92F7).copy(alpha = 0.1f)
                                )
                            )
                        )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App logo and name
            Image(
                painter = painterResource(id = R.drawable.app),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Ping Me",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Create your account",
                color = Color(0xFFE0E0E0),
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Card containing form fields
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // username field

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Username"
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4E92F7),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                            focusedLabelColor = Color(0xFF4E92F7),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                            cursorColor = Color(0xFF4E92F7),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White.copy(alpha = 0.9f)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // email field

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email"
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4E92F7),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                            focusedLabelColor = Color(0xFF4E92F7),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                            cursorColor = Color(0xFF4E92F7),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White.copy(alpha = 0.9f)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // password field

                    // Password validation state
                    var isPasswordValid by remember { mutableStateOf(false) }

                    // Function to validate password
                    fun validatePassword(input: String): Boolean {
                        val hasSpecialChar = input.any { !it.isLetterOrDigit() }
                        return input.length >= 8 && hasSpecialChar
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            isPasswordValid = validatePassword(it)
                        },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password"
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isPasswordValid || password.isEmpty()) Color(0xFF4E92F7) else Color.Red,
                            unfocusedBorderColor = if (isPasswordValid || password.isEmpty()) Color.White.copy(alpha = 0.6f) else Color.Red.copy(alpha = 0.8f),
                            focusedLabelColor = if (isPasswordValid || password.isEmpty()) Color(0xFF4E92F7) else Color.Red,
                            unfocusedLabelColor = if (isPasswordValid || password.isEmpty()) Color.White.copy(alpha = 0.8f) else Color.Red.copy(alpha = 0.8f),
                            cursorColor = Color(0xFF4E92F7),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                            errorBorderColor = Color.Red,
                            errorLabelColor = Color.Red,
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        isError = password.isNotEmpty() && !isPasswordValid,
                        supportingText = {
                            if (password.isNotEmpty() && !isPasswordValid) {
                                Text(
                                    text = "Password must be at least 8 characters and contain special symbols",
                                    color = Color.Red,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            when {
                                username.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                                    errorMessage = "All fields are required"
                                }
                                !email.contains("@") -> {
                                    errorMessage = "Please enter a valid email address"
                                }
                                else -> {
                                    isLoading = true
                                    errorMessage = null

                                    coroutineScope.launch {
                                        try {
                                            val user = signUpData(
                                                username = username,
                                                fullname = fullName,
                                                password = password,
                                                mail = email
                                            )

                                            val response = RetrofitSignUpClient.apiService.registerUser(user)

                                            // Log and display success message
                                            Log.d("SignUpScreen", "Registration successful")
                                            Toast.makeText(context, "Registration successful", Toast.LENGTH_LONG).show()

                                            successMessage = "Account created successfully!"
                                            // Clear form fields after successful signup
                                            username = ""
                                            fullName = ""
                                            email = ""
                                            password = ""
                                            confirmPassword = ""
                                        } catch (e: Exception) {
                                            // Log and display error message
                                            Log.e("SignUpScreen", "Error during registration: ${e.message}")
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                            errorMessage = "Error: ${e.message}"
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E92F7))
                    ) {
                        Text(
                            text = "Sign Up",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "Already have an account? ",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                TextButton(onClick = {
                    goToLoginScreen()
                }) {
                    Text(
                        text = "Login",
                        color = Color(0xFF4E92F7),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

