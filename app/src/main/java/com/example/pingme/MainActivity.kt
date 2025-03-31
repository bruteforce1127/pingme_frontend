package com.example.pingme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pingme.Auth.LoginScreen
import com.example.pingme.Auth.SignUpScreen
import com.example.pingme.ui.theme.PingMeTheme

class MainActivity : ComponentActivity() {
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



@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Set up the navigation graph
    NavHost(
        navController = navController,
        startDestination = "signup"
    ) {
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
                }
            )
        }
    }
}

