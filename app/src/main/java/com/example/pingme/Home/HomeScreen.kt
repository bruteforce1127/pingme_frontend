package com.example.pingme.Home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pingme.TokenSaving.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    goToSetReminderScreen: () -> Unit,
    goToHistoryScreen: (username : String) -> Unit,
    goToInsightsScreen: () -> Unit,
    goToNotificationScreen: () -> Unit,
    goToSignUpScreen : ()->Unit,
    username: String
) {
    // State for selected navigation item
    var selectedNavItem by remember { mutableStateOf(0) }

    // State for sidebar
    var showSidebar by remember { mutableStateOf(false) }

    // Animation states
    var isAnimationComplete by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    // For notification badge
    var hasUnreadNotifications by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    // Define gradients similar to the login screen
    val secondaryGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F7FA), Color(0xFFE4EBF5))
    )

    val primaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4776E6), Color(0xFF8E54E9)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    LaunchedEffect(key1 = Unit) {
        delay(100)
        isAnimationComplete = true
        delay(300)
        showContent = true
    }

    val backgroundScale by animateFloatAsState(
        targetValue = if (isAnimationComplete) 1f else 1.1f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "backgroundScale"
    )

    // Sidebar animation
    val sidebarOffset by animateFloatAsState(
        targetValue = if (showSidebar) 0f else -300f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "sidebarOffset"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") }, // Empty title, using custom content
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                ),
                actions = {
                    // Notification Icon with badge
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                goToNotificationScreen()
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFF4776E6),
                            modifier = Modifier.size(32.dp)
                        )

                        // Notification badge
                        if (hasUnreadNotifications) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF5252))
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                },
                navigationIcon = {
                    // User profile icon with username
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable {
                                showSidebar = true
                            }
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
                }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(75.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
                    .shadow(elevation = 16.dp),
                containerColor = Color.White,
                contentColor = Color(0xFF4776E6)
            ) {
                val navItems = listOf(
                    NavItem("Home", Icons.Outlined.Home),
                    NavItem("Add", Icons.Outlined.AddCircle),
                    NavItem("Insights", Icons.Outlined.Analytics),
                    NavItem("History", Icons.Outlined.History)
                )

                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedNavItem == index,
                        onClick = {
                            selectedNavItem = index
                            when (item.label) {
                                "Home" -> {}
                                "Add" -> goToSetReminderScreen()
                                "Insights" -> goToInsightsScreen()
                                "History" -> goToHistoryScreen(username)
                            }
                        },
                        icon = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(if (selectedNavItem == index) 46.dp else 40.dp)
                                        .background(
                                            if (selectedNavItem == index) {
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        Color(0xFF4776E6).copy(alpha = 0.2f),
                                                        Color(0xFF8E54E9).copy(alpha = 0.1f)
                                                    )
                                                )
                                            } else {
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.Transparent
                                                    )
                                                )
                                            },
                                            shape = CircleShape
                                        )
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = if (selectedNavItem == index) Color(0xFF4776E6) else Color(0xFF696969),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Text(
                                    text = item.label,
                                    color = if (selectedNavItem == index) Color(0xFF4776E6) else Color(0xFF696969),
                                    fontSize = 10.sp
                                )
                            }
                        },
                        label = null,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF4776E6),
                            unselectedIconColor = Color(0xFF696969),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(secondaryGradient)
                .scale(backgroundScale)
        ) {
            // Main content
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(1000, 300)) + slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = tween(1000, 300)
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        // Animated welcome card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 8.dp
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .background(primaryGradient)
                                    .padding(24.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Welcome back,",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = username,
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "You have 3 upcoming reminders",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Today's reminders section
                    item {
                        Text(
                            text = "TODAY'S REMINDERS",
                            color = Color(0xFF333333),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 4.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Project Meeting",
                                    color = Color(0xFF333333),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "2:00 PM - Discuss project timelines",
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
                                            .background(Color(0xFFFF9800).copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Medium Priority",
                                            color = Color(0xFFFF9800),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Upcoming reminders section
                    item {
                        Text(
                            text = "UPCOMING",
                            color = Color(0xFF333333),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 4.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Client Call",
                                    color = Color(0xFF333333),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Tomorrow, 11:00 AM",
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
                                            .background(Color(0xFFF44336).copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "High Priority",
                                            color = Color(0xFFF44336),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Submit Report",
                                    color = Color(0xFF333333),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Friday, 4:00 PM - Send weekly stats to manager",
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
                                            .background(Color(0xFF4CAF50).copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Low Priority",
                                            color = Color(0xFF4CAF50),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Empty space at bottom for better scrolling
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            // Animated Sidebar with slide-in effect
            if (showSidebar) {
                // Semi-transparent overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { showSidebar = false }
                ) {
                    // Sidebar with animation
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(280.dp)
                            .graphicsLayer {
                                translationX = sidebarOffset
                            }
                            .clickable { /* Prevent clicks from passing through */ }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                // User info section
                                Spacer(modifier = Modifier.height(60.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(primaryGradient)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AccountCircle,
                                            contentDescription = "User Profile",
                                            tint = Color.White,
                                            modifier = Modifier.size(70.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = username,
                                        color = Color(0xFF333333),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }

                                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                                Spacer(modifier = Modifier.height(24.dp))

                                // Logout button
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            coroutineScope.launch {
                                                TokenManager.deleteToken(context)
                                                goToSignUpScreen()
                                            }
                                        }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ExitToApp,
                                        contentDescription = "Logout",
                                        tint = Color(0xFFF44336),
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = "Logout",
                                        color = Color(0xFFF44336),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
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

data class NavItem(val label: String, val icon: ImageVector)