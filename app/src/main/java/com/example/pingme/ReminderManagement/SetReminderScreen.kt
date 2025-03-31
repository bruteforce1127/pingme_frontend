import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen() {
    // States for the input fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(Date()) }
    var priority by remember { mutableStateOf("Low") }
    var showPriorityDropdown by remember { mutableStateOf(false) }

    // Animation states
    var isAnimationComplete by remember { mutableStateOf(false) }
    var isFormVisible by remember { mutableStateOf(false) }
    val formScale = remember { Animatable(0.95f) }
    val formAlpha = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Date formatter
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    // Animated floating bubbles
    val bubbleAnimations = List(6) { index ->
        remember { Animatable(initialValue = 0f) }
    }

    // Start animations
    LaunchedEffect(Unit) {
        delay(100)
        isAnimationComplete = true

        delay(300)
        isFormVisible = true

        formScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(500, easing = EaseOutBack)
        )

        formAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(500)
        )

        // Animate floating bubbles
        bubbleAnimations.forEachIndexed { index, animatable ->
            launch {
                delay(index * 50L)
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(800, easing = EaseInOutSine)
                )

                while (true) {
                    animatable.animateTo(
                        targetValue = 0.9f,
                        animationSpec = tween(2000, easing = EaseInOutSine)
                    )
                    animatable.animateTo(
                        targetValue = 1.1f,
                        animationSpec = tween(2000, easing = EaseInOutSine)
                    )
                }
            }
        }
    }

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }

    // Success animation
    var showSuccessAnimation by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background with gradient and animated elements
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
            // Background canvas with animations
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Additional decorative elements could be drawn here
            }

            // Animated floating bubbles
            (0..5).forEach { index ->
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
                val animationValue = bubbleAnimations[index].value

                Box(
                    modifier = Modifier
                        .size(size * animationValue)
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

        // Reminder form content
        AnimatedVisibility(
            visible = isFormVisible,
            enter = fadeIn(animationSpec = tween(500)) + expandVertically(
                animationSpec = tween(500, easing = EaseOutCubic)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header
                Text(
                    text = "Set Reminder",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Form card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(formScale.value)
                        .alpha(formAlpha.value)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title field
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1488CC),
                                unfocusedBorderColor = Color(0xFF1488CC).copy(alpha = 0.5f),
                                focusedLabelColor = Color(0xFF1488CC),
                                cursorColor = Color(0xFF1488CC)
                            )
                        )

                        // Description field
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1488CC),
                                unfocusedBorderColor = Color(0xFF1488CC).copy(alpha = 0.5f),
                                focusedLabelColor = Color(0xFF1488CC),
                                cursorColor = Color(0xFF1488CC)
                            )
                        )

                        // Date field
                        OutlinedTextField(
                            value = dateFormatter.format(date),
                            onValueChange = { },
                            label = { Text("Date") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1488CC),
                                unfocusedBorderColor = Color(0xFF1488CC).copy(alpha = 0.5f),
                                focusedLabelColor = Color(0xFF1488CC),
                                cursorColor = Color(0xFF1488CC)
                            ),
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.DateRange,
                                    contentDescription = "Select Date",
                                    tint = Color(0xFF1488CC)
                                )
                            }
                        )

                        // Priority field
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = priority,
                                onValueChange = { },
                                label = { Text("Priority") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showPriorityDropdown = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1488CC),
                                    unfocusedBorderColor = Color(0xFF1488CC).copy(alpha = 0.5f),
                                    focusedLabelColor = Color(0xFF1488CC),
                                    cursorColor = Color(0xFF1488CC)
                                ),
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Priority",
                                        tint = Color(0xFF1488CC)
                                    )
                                }
                            )

                            DropdownMenu(
                                expanded = showPriorityDropdown,
                                onDismissRequest = { showPriorityDropdown = false },
                                modifier = Modifier
                                    .width(with(LocalDensity.current) {
                                        (300.dp).toPx().toInt().toDp()
                                    })
                                    .background(Color.White)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Low") },
                                    onClick = {
                                        priority = "Low"
                                        showPriorityDropdown = false
                                    },
                                    leadingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(Color(0xFF4CAF50), CircleShape)
                                        )
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("High") },
                                    onClick = {
                                        priority = "High"
                                        showPriorityDropdown = false
                                    },
                                    leadingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(Color(0xFFF44336), CircleShape)
                                        )
                                    }
                                )
                            }
                        }

                        // Submit button
                        Button(
                            onClick = {
                                // Show success animation
                                showSuccessAnimation = true
                                scope.launch {
                                    delay(2000)
                                    showSuccessAnimation = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2B32B2)
                            )
                        ) {
                            Text(
                                "Set Reminder",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Date picker dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                onDateSelected = { selectedDate ->
                    date = selectedDate
                    showDatePicker = false
                }
            )
        }

        // Success animation
        AnimatedVisibility(
            visible = showSuccessAnimation,
            enter = fadeIn(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                SuccessAnimation()
            }
        }
    }
}

@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Date",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B32B2)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Simple date selector (in a real app you would use DatePicker)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Day
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Day", fontSize = 14.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                                }
                            ) {
                                Text("-", fontSize = 24.sp, color = Color(0xFF2B32B2))
                            }

                            Text(
                                text = calendar.get(Calendar.DAY_OF_MONTH).toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = {
                                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                                }
                            ) {
                                Text("+", fontSize = 24.sp, color = Color(0xFF2B32B2))
                            }
                        }
                    }

                    // Month
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Month", fontSize = 14.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    calendar.add(Calendar.MONTH, -1)
                                }
                            ) {
                                Text("-", fontSize = 24.sp, color = Color(0xFF2B32B2))
                            }

                            Text(
                                text = (calendar.get(Calendar.MONTH) + 1).toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = {
                                    calendar.add(Calendar.MONTH, 1)
                                }
                            ) {
                                Text("+", fontSize = 24.sp, color = Color(0xFF2B32B2))
                            }
                        }
                    }

                    // Year
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Year", fontSize = 14.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    calendar.add(Calendar.YEAR, -1)
                                }
                            ) {
                                Text("-", fontSize = 24.sp, color = Color(0xFF2B32B2))
                            }

                            Text(
                                text = calendar.get(Calendar.YEAR).toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = {
                                    calendar.add(Calendar.YEAR, 1)
                                }
                            ) {
                                Text("+", fontSize = 24.sp, color = Color(0xFF2B32B2))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text("Cancel", color = Color.Gray)
                    }

                    TextButton(
                        onClick = {
                            onDateSelected(calendar.time)
                        }
                    ) {
                        Text("OK", color = Color(0xFF2B32B2))
                    }
                }
            }
        }
    }
}

@Composable
fun SuccessAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "success")
    val rotationAnim = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val scaleAnim = infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(200.dp)
            .scale(scaleAnim.value)
            .background(Color.White, CircleShape)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(100.dp)
        ) {
            rotate(rotationAnim.value) {
                drawArc(
                    color = Color(0xFF2B32B2),
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx())
                )
            }

            // Check mark
            val checkPath = Path().apply {
                moveTo(size.width * 0.3f, size.height * 0.5f)
                lineTo(size.width * 0.45f, size.height * 0.7f)
                lineTo(size.width * 0.7f, size.height * 0.3f)
            }

            drawPath(
                path = checkPath,
                color = Color(0xFF4CAF50),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
        }
    }

    // Add text below
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Reminder Set!",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}