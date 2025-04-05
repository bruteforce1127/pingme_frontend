package com.example.pingme.Reminder

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.auth0.android.jwt.JWT
import com.example.pingme.Auth.loginInterface
import com.example.pingme.TokenSaving.AuthInterceptor
import com.example.pingme.TokenSaving.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale




data class ReminderClass(
    var title : String ="",
    var description : String ="",
    var date : Date = Date(),
    var priority : String=""
)


interface ReminderInterface {
    @PUT("/reminderManagement/{username}")
    suspend fun remind(
        @retrofit2.http.Path("username") username: String,
        @Body reminderData:ReminderClass
    ): ReminderClass
}

object RetrofitLoginClient{
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

) {
    // States for form fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var formattedDate by remember { mutableStateOf(formatDate(LocalDate.now())) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading = remember{ mutableStateOf(false) }
    // States for dropdowns
    var isPriorityExpanded by remember { mutableStateOf(false) }
    val priorityOptions = listOf("Low", "Medium", "High")

    // Calendar dialog state
    var showCalendarDialog by remember { mutableStateOf(false) }

    // Animation states
    var isAnimationComplete by remember { mutableStateOf(false) }

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
        modifier = Modifier.fillMaxSize()
    ) {
        // Background
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
                // Abstract wave pattern
                val path = Path()
                val width = size.width
                val height = size.height

                path.moveTo(0f, height * 0.3f)

                for (i in 0..3) {
                    val x1 = width * 0.25f * i
                    val y1 = height * (0.2f + 0.05f * i)
                    val x2 = width * (0.25f * i + 0.125f)
                    val y2 = height * (0.35f - 0.05f * i)
                    val x3 = width * (0.25f * i + 0.25f)
                    val y3 = height * (0.3f + 0.05f * i)

                    path.cubicTo(
                        x1, y1,
                        x2, y2,
                        x3, y3
                    )
                }

                // More abstract points
                (0..20).forEach { i ->
                    val x = (i * 50f) % width
                    val y = (i * 40f) % height
                    drawCircle(
                        color = Color.White.copy(alpha = 0.05f),
                        radius = 20f + (i % 5) * 10f,
                        center = Offset(x, y)
                    )
                }
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
                    // Header
                    Text(
                        text = "Set Reminder",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
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
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Title field
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Title") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Title,
                                        contentDescription = "Title",
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

                            // Description field
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Description") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = "Description",
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
                                minLines = 3,
                                maxLines = 5,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Priority dropdown
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = priority,
                                    onValueChange = { },
                                    readOnly = true,
                                    label = { Text("Priority") },
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
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Show priorities",
                                                tint = Color.White.copy(alpha = 0.8f)
                                            )
                                        }
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
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isPriorityExpanded = !isPriorityExpanded }
                                )

                                DropdownMenu(
                                    expanded = isPriorityExpanded,
                                    onDismissRequest = { isPriorityExpanded = false },
                                    modifier = Modifier
                                        .width(200.dp)
                                        .background(
                                            Color(0xFF1A2151).copy(alpha = 0.95f)
                                        )
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
                                                    Text(
                                                        text = option,
                                                        color = Color.White
                                                    )
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

                            // Date picker field
                            OutlinedTextField(
                                value = formattedDate,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Due Date") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Calendar",
                                        tint = Color.White.copy(alpha = 0.8f)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showCalendarDialog = true }) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = "Open Calendar",
                                            tint = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
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
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showCalendarDialog = true }
                            )
                            val context = LocalContext.current
                            // Save button with gradient
                            Button(
                                onClick = {
                                    when{
                                        title.isEmpty() || description.isEmpty() || formattedDate.isEmpty() || priority.isEmpty()->{
                                            Toast.makeText(context,"All fields are cumpulsary",Toast.LENGTH_LONG).show()
                                        }
                                        else->{
                                            isLoading.value = true
                                            coroutineScope.launch {
                                                try{
                                                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                                    val parsedDate: Date = dateFormat.parse(formattedDate)
                                                        ?: throw IllegalArgumentException("Invalid date")
                                                    val reminderData = ReminderClass(
                                                        title = title,
                                                        description = description,
                                                        date = parsedDate,
                                                        priority = priority
                                                    )

                                                    val apiService = RetrofitLoginClient.getApiService(context)

                                                    val token = TokenManager.getToken(context) ?: return@launch
                                                    var username = extractUsernameFromToken(token)
                                                    val response = apiService.remind(
                                                        username = username.toString(),
                                                        reminderData = reminderData
                                                    )

                                                    Log.d("ReminderScreen","Successfully entered data")

                                                    Toast.makeText(context,"Reminder is set", Toast.LENGTH_LONG).show()

                                                }catch(e:Exception){
                                                    Log.e("ReminderScreen","There was an error sending data",e)
                                                }
                                            }
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
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
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
                                        text = "Save Reminder",
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
                            text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
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
                            text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(1),
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