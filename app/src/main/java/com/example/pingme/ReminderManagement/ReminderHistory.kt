package com.example.pingme.ReminderManagement

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pingme.TokenSaving.AuthInterceptor
import com.example.pingme.TokenSaving.TokenManager
import com.google.gson.GsonBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

data class Reminder(
    val title: String,
    val date: String,
    val priority: Priority
)

data class ReminderHistory(
    val id: Long,
    val title: String,
    val description: String,
    val date: Date,
    val priority: String
)

data class ReminderHistoryList(
    val username: String,
    val remindersList: List<ReminderHistory>
)

interface HistoryApiInterface {
    @GET("/reminderManagement/{username}")
    suspend fun getHistory(
        @Path("username") username: String
    ): ReminderHistoryList
}

object RetrofitHistoryClient {
    private const val BASE_URL = "http://172.22.43.225:8080"

    fun getApiService(context: Context): HistoryApiInterface {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()

        // Create a custom Gson instance with the specified date format
        val gson = GsonBuilder()
            .setDateFormat("MMM dd, yyyy")
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(HistoryApiInterface::class.java)
    }
}

enum class Priority(val color: Color, val label: String) {
    HIGH(Color(0xFFF44336), "High Priority"),
    MEDIUM(Color(0xFFFF9800), "Medium Priority"),
    LOW(Color(0xFF4CAF50), "Low Priority")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReminderHistoryScreen(
    username: String,
    goToSignUpScreen: (username: String) -> Unit
) {
    // Animation state
    var isVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Use stateful lists for reminders so Compose recomposes when data updates
    val todayReminders = remember { mutableStateListOf<ReminderHistory>() }
    val upcomingReminders = remember { mutableStateListOf<ReminderHistory>() }

    // API call to fetch reminder history
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                Log.d("user history","api calling")

                val apiInterface = RetrofitHistoryClient.getApiService(context)
                val token = TokenManager.getToken(context) ?: return@launch
                val extractedUsername = extractUsernameFromToken(token)
                val response = apiInterface.getHistory(extractedUsername.toString())
                val allReminders = response.remindersList

                Log.d("user history","api called successfully")

                // Clear any existing items
                todayReminders.clear()
                upcomingReminders.clear()

                for (reminder in allReminders) {
                    // Convert java.util.Date to LocalDate for comparison
                    val reminderLocalDate = reminder.date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    if (reminderLocalDate == LocalDate.now()) {
                        todayReminders.add(reminder)
                    } else {
                        upcomingReminders.add(reminder)
                    }
                }
            } catch (e: Exception) {
                Log.e("reminderHistory", "Error creating history", e)
            }
        }
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {
            // Top bar with back button and user info
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1000)) +
                        slideInVertically(animationSpec = tween(1000)) { it / 2 }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // User profile icon and username
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(primaryGradient)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AccountCircle,
                                contentDescription = "User Profile",
                                tint = Color.White,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = username,
                            color = Color(0xFF333333),
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp
                        )
                    }
                    // Back button (positioned at the rightmost corner)
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF4776E6),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                goToSignUpScreen(username)
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main title
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1200)) +
                        slideInVertically(animationSpec = tween(1200)) { it / 2 }
            ) {
                Text(
                    text = "Reminder History",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Today's reminders section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1300)) +
                        slideInVertically(animationSpec = tween(1300)) { it / 2 }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "TODAY'S REMINDERS",
                        color = Color(0xFF333333),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )
                    todayReminders.forEach { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onClick = { /* Handle reminder click */ }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1400))
            ) {
                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upcoming reminders section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1500)) +
                        slideInVertically(animationSpec = tween(1500)) { it / 2 }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "UPCOMING REMINDERS",
                        color = Color(0xFF333333),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )
                    upcomingReminders.forEach { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onClick = { /* Handle reminder click */ }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // Bottom spacer for better scrolling
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ReminderCard(
    reminder: ReminderHistory,
    onClick: () -> Unit
) {
    // Convert the Date to a formatted String
    val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(reminder.date)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = reminder.title,
                color = Color(0xFF333333),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formattedDate,
                color = Color(0xFF696969),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(reminder.priority.let { prio ->
                            when (prio.uppercase(Locale.getDefault())) {
                                "HIGH" -> Priority.HIGH.color.copy(alpha = 0.2f)
                                "MEDIUM" -> Priority.MEDIUM.color.copy(alpha = 0.2f)
                                "LOW" -> Priority.LOW.color.copy(alpha = 0.2f)
                                else -> Color.Gray.copy(alpha = 0.2f)
                            }
                        })
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = reminder.priority,
                        color = when (reminder.priority.uppercase(Locale.getDefault())) {
                            "HIGH" -> Priority.HIGH.color
                            "MEDIUM" -> Priority.MEDIUM.color
                            "LOW" -> Priority.LOW.color
                            else -> Color.Gray
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
