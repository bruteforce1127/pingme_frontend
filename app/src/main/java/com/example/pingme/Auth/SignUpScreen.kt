package com.example.pingme.Auth

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Define your user model
data class UserModel(
    val username: String,
    val fullName: String,
    val password: String,
    val mail: String
)

// Define your API interface
interface UserApiService {
    @POST("/register")
    suspend fun registerUser(@Body user: UserModel): UserModel
}

// Create a Retrofit instance
object RetrofitClient {
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
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    // For animations
    var isVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Start animation after a short delay
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Define gradients
    val primaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4776E6), Color(0xFF8E54E9)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    val secondaryGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F7FA), Color(0xFFE4EBF5))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(secondaryGradient)
    ) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Animated app logo
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1000)) +
                        slideInVertically(animationSpec = tween(1000)) { it / 2 }
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(elevation = 20.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(primaryGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "PM",
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 40.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Animated title
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1200)) +
                        slideInVertically(animationSpec = tween(1200)) { it / 2 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Create Account",
                        style = TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333),
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        "Sign up to organize your life with reminders",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF696969),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
                    )
                }
            }

            // Define form fields as a list of composable lambdas
            val formFields: List<@Composable () -> Unit> = listOf(
                {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(1300)) +
                                slideInVertically(animationSpec = tween(1300)) { it / 2 }
                    ) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8E54E9),
                                unfocusedBorderColor = Color.Black,
                                focusedTextColor = Color.Black

                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            singleLine = true,

                            )
                    }
                },
//                {
//                    AnimatedVisibility(
//                        visible = isVisible,
//                        enter = fadeIn(animationSpec = tween(1400)) +
//                                slideInVertically(animationSpec = tween(1400)) { it / 2 }
//                    ) {
//                        OutlinedTextField(
//                            value = fullName,
//                            onValueChange = { fullName = it },
//                            label = { Text("Full Name") },
//                            leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = "Full Name") },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(bottom = 20.dp),
//                            shape = RoundedCornerShape(16.dp),
//                            colors = OutlinedTextFieldDefaults.colors(
//                                focusedBorderColor = Color(0xFF8E54E9),
//                                unfocusedBorderColor = Color(0xFFE0E0E0)
//                            ),
//                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
//                            singleLine = true
//                        )
//                    }
//                },
                {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(1500)) +
                                slideInVertically(animationSpec = tween(1500)) { it / 2 }
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8E54E9),
                                unfocusedBorderColor = Color.Black,
                                focusedTextColor = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true
                        )
                    }
                },
                {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(1600)) +
                                slideInVertically(animationSpec = tween(1600)) { it / 2 }
                    ) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                            // Removed trailing icon for simplicity (or replace with supported icon if needed)
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8E54E9),
                                unfocusedBorderColor = Color.Black,
                                focusedTextColor = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true
                        )
                    }
                }
            )

            // Display form fields
            formFields.forEach { it() }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(visible = errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = Color(0xFFE53935),
                    modifier = Modifier.padding(bottom = 16.dp),
                    style = TextStyle(fontSize = 14.sp)
                )
            }

            AnimatedVisibility(visible = successMessage != null) {
                Text(
                    text = successMessage ?: "",
                    color = Color(0xFF43A047),
                    modifier = Modifier.padding(bottom = 16.dp),
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1800)) +
                        slideInVertically(animationSpec = tween(1800)) { it / 2 }
            ) {
                Button(
                    onClick = {
                        when {
                            username.isEmpty() || fullName.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                                errorMessage = "All fields are required"
                                Toast.makeText(context,"All fields are Required",Toast.LENGTH_LONG).show()
                            }
                            !email.contains("@") -> {
                                errorMessage = "Please enter a valid email address"
                                Toast.makeText(context,"Please enter a valid email address",Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                isLoading = true
                                errorMessage = null

                                coroutineScope.launch {
                                    try {
                                        val user = UserModel(
                                            username = username,
                                            fullName = fullName,
                                            password = password,
                                            mail = email
                                        )

                                        val response = RetrofitClient.apiService.registerUser(user)
                                        // Log and display success message
                                        Log.d("SignUpScreen", "Registration successful")
                                        Toast.makeText(context, "Registration successful", Toast.LENGTH_LONG).show()

                                        successMessage = "Account created successfully!"
                                        isLoading = false

                                        // Clear form fields
                                        username = ""
                                        fullName = ""
                                        email = ""
                                        password = ""

                                        goToLoginScreen()
                                    } catch (e: Exception) {
                                        errorMessage = "Error: ${e.message}"
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    enabled = !isLoading
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(primaryGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "SIGN UP",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(2000))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Already have an account?",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color(0xFF696969)
                        )
                    )
                    TextButton(onClick = {
                        goToLoginScreen()
                    }) {
                        Text(
                            "Login",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8E54E9)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}