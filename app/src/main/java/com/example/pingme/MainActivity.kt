package com.example.pingme

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.auth0.android.jwt.JWT
import com.example.pingme.Auth.LoginScreen
import com.example.pingme.Auth.SignUpScreen
import com.example.pingme.Auth.extractUsernameFromToken
import com.example.pingme.Home.HomeScreen
import com.example.pingme.Reminder.ReminderScreen
import com.example.pingme.TokenSaving.TokenManager
import com.example.pingme.ui.theme.PingMeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PingMeTheme {
                AppNavigation()
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current

    // Set up the navigation graph
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController, context)
        }
        composable(
            "signup"
        ) {
            SignUpScreen(
                goToLoginScreen = {
                    navController.navigate("login") }
            )
        }
        composable(
            "login"
        ) {
            LoginScreen(
                goToSignUpScreen = {
                    navController.navigate("signup")
                },
                goToHomeScreen = {username->
                    navController.navigate("homeScreen/$username")
                }
            )
        }
        composable(
            route = "homeScreen/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")?.let { Uri.decode(it) } ?: ""
            HomeScreen(
                goToSetReminderScreen = {
                    navController.navigate("ReminderScreen")
                },
                goToHistoryScreen = { /*TODO*/ },
                goToInsightsScreen = { /*TODO*/ },
                goToNotificationScreen = {},
                username = username
            )
        }
        composable(
            route = "ReminderScreen"
        ) {
            ReminderScreen()
        }
    }
}


fun isTokenExpired(token: String): Boolean {
    return try {
        val jwt = JWT(token)
        jwt.expiresAt?.before(Date()) ?: true  // If there's no expiration date, treat as expired
    } catch (e: Exception) {
        // If token is invalid, consider it expired
        true
    }
}



@Composable
fun SplashScreen(navController: NavController, context: Context) {
    // Animation states
    val scale = remember { Animatable(0.3f) }
    val alpha = remember { Animatable(0f) }
    val rotationAngle = remember { Animatable(0f) }

    // App theme colors
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    // Play animations sequentially
    LaunchedEffect(Unit) {
        // First, scale up and fade in the logo
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700)
            )
        }

        // Then add rotation to the rings
        delay(300)
        launch {
            rotationAngle.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
            )
        }

        // Check auth token and navigate after animations
        delay(1800)
        val token = TokenManager.getToken(context)
        if (token != null && !isTokenExpired(token)) {
            val username = extractUsernameFromToken(token)
            navController.navigate("homeScreen/$username") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("signup") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // UI while waiting for token check
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Animated content
        Box(
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value),
            contentAlignment = Alignment.Center
        ) {
            // Rotating rings
            Canvas(modifier = Modifier.size(200.dp)) {
                // Outer ring
                drawArc(
                    color = primaryColor,
                    startAngle = rotationAngle.value,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx()),
                    size = Size(size.width, size.height)
                )

                // Middle ring
                drawArc(
                    color = secondaryColor,
                    startAngle = -rotationAngle.value + 45f,
                    sweepAngle = 240f,
                    useCenter = false,
                    style = Stroke(width = 6.dp.toPx()),
                    size = Size(size.width * 0.75f, size.height * 0.75f),
                    topLeft = Offset(size.width * 0.125f, size.height * 0.125f)
                )

                // Inner ring
                drawArc(
                    color = tertiaryColor,
                    startAngle = rotationAngle.value + 90f,
                    sweepAngle = 180f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx()),
                    size = Size(size.width * 0.5f, size.height * 0.5f),
                    topLeft = Offset(size.width * 0.25f, size.height * 0.25f)
                )
            }

            // Central logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Message icon
                    Icon(
                        imageVector = Icons.Rounded.Chat,
                        contentDescription = "Ping Me Icon",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App Name with glowing effect
                val infiniteTransition = rememberInfiniteTransition()
                val glowAlpha = infiniteTransition.animateFloat(
                    initialValue = 0.6f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Text(
                    text = "Ping Me",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .alpha(glowAlpha.value)
                        .graphicsLayer {
                            shadowElevation = 8f
                            shape = RoundedCornerShape(8.dp)
                        }
                )
            }
        }
    }
}

