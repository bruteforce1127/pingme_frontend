package com.example.pingme.ReminderManagement

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.auth0.android.jwt.JWT
import com.example.pingme.TokenSaving.AuthInterceptor
import com.example.pingme.TokenSaving.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.PUT
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

data class ReminderClass(
    var title: String = "",
    var description: String = "",
    var date: Date = Date(),
    var priority: String = ""
)

interface ReminderInterface {
    @PUT("/reminderManagement/{username}")
    suspend fun remind(
        @retrofit2.http.Path("username") username: String,
        @Body reminderData: ReminderClass
    ): ReminderClass
}

object RetrofitReminderClient {
    private const val BASE_URL = "http://172.22.43.225:8080"

    fun getApiService(context: Context): ReminderInterface {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReminderInterface::class.java)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReminderScreen(
    goToHomeScreen : (username : String)->Unit
) {
    // States for form fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var formattedDate by remember { mutableStateOf(formatDate(LocalDate.now())) }
    var isLoading by remember { mutableStateOf(false) }

    // States for dropdowns
    var isPriorityExpanded by remember { mutableStateOf(false) }
    val priorityOptions = listOf("Low", "Medium", "High")

    // Calendar dialog state
    var showCalendarDialog by remember { mutableStateOf(false) }

    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Start animation after a short delay
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Define gradients - using LoginScreen style
    val primaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4776E6), Color(0xFF8E54E9)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    val secondaryGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F7FA), Color(0xFFE4EBF5))
    )

    // Calendar dialog
    if (showCalendarDialog) {
        CustomCalendarDialog(
            selectedDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
                formattedDate = formatDate(date)
                showCalendarDialog = false
            },
            onDismiss = { showCalendarDialog = false }
        )
    }

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
            Spacer(modifier=Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end= 320.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            val token = TokenManager.getToken(context)
                            val username = extractUsernameFromToken(token.toString())
                            goToHomeScreen(username.toString())
                        }
                    }
                        .size(140.dp)
                )
            }

            // Animated title
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1200)) +
                        slideInVertically(animationSpec = tween(1200)) { it / 2 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Set Reminder",
                        style = TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333),
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        "Create a new reminder for your tasks",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF696969),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
                    )
                }
            }

            // Title field
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1300)) +
                        slideInVertically(animationSpec = tween(1300)) { it / 2 }
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title" , color = Color.Black) },
                    leadingIcon = { Icon(Icons.Default.Title, contentDescription = "Title", tint = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8E54E9),
                        unfocusedBorderColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                )
            }

            // Description field
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1400)) +
                        slideInVertically(animationSpec = tween(1400)) { it / 2 }
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", color = Color.Black) },
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = "Description", tint = Color.Black) 
                                  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8E54E9),
                        unfocusedBorderColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    minLines = 3,
                    maxLines = 5
                )
            }

            // Priority field
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1500)) +
                        slideInVertically(animationSpec = tween(1500)) { it / 2 }
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Priority", color = Color.Black) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Priority",
                                tint = when(priority) {
                                    "Low" -> Color(0xFF4CAF50)
                                    "High" -> Color(0xFFF44336)
                                    else -> Color(0xFFFF9800)
                                }
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPriorityExpanded = !isPriorityExpanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Show priorities", tint = Color.Black)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clickable { isPriorityExpanded = !isPriorityExpanded },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8E54E9),
                            unfocusedBorderColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        singleLine = true,
                    )

                    DropdownMenu(
                        expanded = isPriorityExpanded,
                        onDismissRequest = { isPriorityExpanded = false },
                        modifier = Modifier
                            .width(200.dp)
                            .background(Color.White)
                    ) {
                        priorityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Flag,
                                            contentDescription = option,
                                            tint = when(option) {
                                                "Low" -> Color(0xFF4CAF50)
                                                "High" -> Color(0xFFF44336)
                                                else -> Color(0xFFFF9800)
                                            },
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = option)
                                    }
                                },
                                onClick = {
                                    priority = option
                                    isPriorityExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Date field
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1600)) +
                        slideInVertically(animationSpec = tween(1600)) { it / 2 }
            ) {
                OutlinedTextField(
                    value = formattedDate,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Due Date", color = Color.Black) },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar" , tint = Color.Black) },
                    trailingIcon = {
                        IconButton(onClick = { showCalendarDialog = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Open Calendar", tint = Color.Black)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .clickable { showCalendarDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8E54E9),
                        unfocusedBorderColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    singleLine = true,
                )
            }
            
            Spacer(modifier = Modifier.height(60.dp))

            // Save button
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1700)) +
                        slideInVertically(animationSpec = tween(1700)) { it / 2 }
            ) {
                Button(
                    onClick = {
                        when {
                            title.isEmpty() || description.isEmpty() || formattedDate.isEmpty() || priority.isEmpty() -> {
                                Toast.makeText(context, "All fields are compulsory", Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                isLoading = true
                                coroutineScope.launch {
                                    try {
                                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                        val parsedDate: Date = dateFormat.parse(formattedDate)
                                            ?: throw IllegalArgumentException("Invalid date")
                                        val reminderData = ReminderClass(
                                            title = title,
                                            description = description,
                                            date = parsedDate,
                                            priority = priority
                                        )

                                        val apiService =
                                            RetrofitReminderClient.getApiService(context)

                                        val token = TokenManager.getToken(context) ?: return@launch
                                        val username = extractUsernameFromToken(token)
                                        val response = apiService.remind(
                                            username = username.toString(),
                                            reminderData = reminderData
                                        )

                                        Log.d("ReminderScreen", "Successfully entered data")
                                        Toast.makeText(context, "Reminder is set", Toast.LENGTH_LONG).show()

                                        // Reset fields after successful save
                                        title = ""
                                        description = ""
                                        priority = "Medium"
                                        selectedDate = LocalDate.now()
                                        formattedDate = formatDate(LocalDate.now())
                                        isLoading = false

                                    } catch (e: Exception) {
                                        Log.e("ReminderScreen", "There was an error sending data", e)
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
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
                                "SAVE REMINDER",
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

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomCalendarDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    var selectedLocalDate by remember { mutableStateOf(selectedDate) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A2151)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Calendar header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            currentMonth = currentMonth.minusMonths(1)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous Month",
                            tint = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentMonth.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault()),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentMonth.year.toString(),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 16.sp
                        )
                    }

                    IconButton(
                        onClick = {
                            currentMonth = currentMonth.plusMonths(1)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next Month",
                            tint = Color.White
                        )
                    }
                }

                // Days header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (day in DayOfWeek.values()) {
                        Text(
                            text = day.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()).take(1),
                            modifier = Modifier.weight(1f),
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Calendar grid
                val firstDayOfMonth = currentMonth.atDay(1)
                val lastDayOfMonth = currentMonth.atEndOfMonth()
                val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

                val daysInMonth = (1..lastDayOfMonth.dayOfMonth).toList()
                val calendarDays = List(firstDayOfWeek) { null } + daysInMonth

                // Create rows for each week
                for (weekIndex in 0 until (calendarDays.size + 6) / 7) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (dayIndex in 0 until 7) {
                            val index = weekIndex * 7 + dayIndex
                            if (index < calendarDays.size) {
                                val day = calendarDays[index]
                                if (day != null) {
                                    val date = currentMonth.atDay(day)
                                    val isSelected = date == selectedLocalDate
                                    val isToday = date == LocalDate.now()

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isSelected -> Color(0xFF8E54E9)
                                                    isToday -> Color(0xFF4776E6).copy(alpha = 0.3f)
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .border(
                                                width = if (isToday && !isSelected) 1.dp else 0.dp,
                                                color = if (isToday && !isSelected) Color(0xFF4776E6) else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                selectedLocalDate = date
                                                onDateSelected(date)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = day.toString(),
                                            color = when {
                                                isSelected -> Color.White
                                                else -> Color.White.copy(alpha = 0.8f)
                                            },
                                            fontSize = 14.sp
                                        )
                                    }
                                } else {
                                    // Empty space for days from previous/next month
                                    Box(modifier = Modifier.size(32.dp))
                                }
                            } else {
                                // Empty space for remaining cells
                                Box(modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            selectedLocalDate = LocalDate.now()
                            onDateSelected(LocalDate.now())
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4776E6)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Today")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8E54E9)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return date.format(formatter)
}

fun extractUsernameFromToken(jwtToken: String): String? {
    return try {
        val jwt = JWT(jwtToken)
        // Assumes the username is stored in the "sub" or "username" claim
        jwt.getClaim("sub").asString() ?: jwt.getClaim("username").asString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}