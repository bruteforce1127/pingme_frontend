package com.example.pingme.Auth

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


data class loginData(
    var username:String,
    var password:String
)

interface loginInterface{
    @POST("/login")
    suspend fun loginUser(@Body user: loginData) : String
}

object RetrofitLoginClient{
    private const val BASE_URL = "http://172.22.78.106:8080"

    val apiService: loginInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(loginInterface::class.java)
    }
}

@Composable
fun LoginScreen(
    goToSignUpScreen : ()->Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var authT by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isAnimationComplete by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Animation states
    LaunchedEffect(key1 = Unit) {
        delay(100)
        isAnimationComplete = true
    }

    val backgroundScale by animateFloatAsState(
        targetValue = if (isAnimationComplete) 1f else 1.1f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "backgroundScale"
    )

    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isAnimationComplete) 1f else 0.6f,
        animationSpec = tween(1200),
        label = "backgroundAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Gorgeous background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(backgroundScale)
                .alpha(backgroundAlpha)
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
            // Decorative elements
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Abstract decorative shapes would be drawn here
                // This is a placeholder for custom drawing
            }

            // Floating circles with blur effect
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

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = isAnimationComplete,
                enter = fadeIn(tween(1000)) + slideInVertically(
                    initialOffsetY = { -100 },
                    animationSpec = tween(1000)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App logo
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 24.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.2f),
                                        Color.White.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(80.dp)
                                .padding(8.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // App name
                    Text(
                        text = "Ping Me",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Welcome text
                    Text(
                        text = "Welcome back",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 40.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = isAnimationComplete,
                enter = fadeIn(tween(1000, 300)) + slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = tween(1000, 300)
                )
            ) {
                // Card with glass effect
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.2f),
                                        Color.White.copy(alpha = 0.05f)
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Username field
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = { Text("Username") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Username",
                                        tint = Color.White.copy(alpha = 0.8f)
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                                    cursorColor = Color.White,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Password field
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Password",
                                        tint = Color.White.copy(alpha = 0.8f)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                                            tint = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                                    cursorColor = Color.White,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Forgot password text
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                TextButton(
                                    onClick = { /* Forgot password action */ },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = Color.White.copy(alpha = 0.9f)
                                    )
                                ) {
                                    Text(
                                        text = "Forgot Password?",
                                        fontSize = 14.sp
                                    )
                                }
                            }

//                            Spacer(modifier = Modifier.height(4.dp))

                            // Login button with gradient
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        try{
                                            val user = loginData(
                                                username=username,
                                                password=password
                                            )
                                            val response = RetrofitLoginClient.apiService.loginUser(user)

                                            if (response == "failure") {
                                                Log.d("Login", "Wrong Credentials")
                                            } else {
                                                authT = response // Store the JWT token
                                                Log.d("Login", "Token: $authT")
                                            }
                                        } catch (e: Exception) {
                                            Log.e("Login", "Error: ${e.message}")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFF4776E6),
                                                    Color(0xFF8E54E9)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Log In",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isAnimationComplete,
                enter = fadeIn(tween(1000, 600))
            ) {
                // Don't have an account
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = "Don't have an account? ",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )

                    TextButton(onClick = {
                        goToSignUpScreen()
                    }) {
                        Text(
                            text = "Sign Up",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}