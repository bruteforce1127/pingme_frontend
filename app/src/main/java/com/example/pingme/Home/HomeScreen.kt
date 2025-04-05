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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    goToSetReminderScreen : ()->Unit,
    goToHistoryScreen : ()->Unit,
    goToInsightsScreen : ()->Unit,
    goToNotificationScreen : ()->Unit,
    username : String
) {
    // State for selected navigation item
    var selectedNavItem by remember { mutableStateOf(0) }

    // Animation states
    var isAnimationComplete by remember { mutableStateOf(false) }

    // For notification badge
    var hasUnreadNotifications by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        delay(100)
        isAnimationComplete = true
    }

    val backgroundScale by animateFloatAsState(
        targetValue = if (isAnimationComplete) 1f else 1.1f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "backgroundScale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") }, // Empty title, we'll use custom content
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                actions = {
                    // Notification Icon with badge
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                // TODO: Navigate to notifications page
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )

                        // Notification badge
                        if (hasUnreadNotifications) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
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
                                // TODO: Navigate to profile page or show profile menu
                            }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AccountCircle,
                                contentDescription = "User Profile",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = username,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(70.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A2151).copy(alpha = 0.95f),
                                Color(0xFF2B32B2).copy(alpha = 0.95f)
                            )
                        )
                    ),
                containerColor = Color.Transparent,
                contentColor = Color.White
            ) {
                val navItems = listOf(
                    NavItem("Home", Icons.Filled.Home),
                    NavItem("Add Reminder", Icons.Filled.Add),  // Changed to Add icon
                    NavItem("Insights", Icons.Rounded.Assessment),
                    NavItem("History", Icons.Rounded.History)
                )

                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedNavItem == index,
                        onClick = {
                            selectedNavItem = index
                            when (item.label) {
                                "Home" -> goToNotificationScreen()
                                "Add Reminder" -> goToSetReminderScreen()
                                "Insights" -> goToInsightsScreen()
                                "History" -> goToHistoryScreen()
                            }
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(if (selectedNavItem == index) 56.dp else 40.dp)
                                    .background(
                                        if (selectedNavItem == index) {
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    Color(0xFF4776E6).copy(alpha = 0.7f),
                                                    Color(0xFF8E54E9).copy(alpha = 0.4f)
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
                                    tint = if (selectedNavItem == index) Color.White else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        label = null,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
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
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2B32B2),
                            Color(0xFF1488CC),
                            Color(0xFF2B32B2)
                        )
                    )
                )
                .scale(backgroundScale)
        ) {
            AnimatedVisibility(
                visible = isAnimationComplete,
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
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // Greeting header
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "Welcome back,",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Username",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "You have 3 reminders for today",
                                    color = Color(0xFF4E92F7),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // TODO: Add more items to LazyColumn as per your app requirements
                    items(10) { index ->
                        // Placeholder for content items
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

data class NavItem(val label: String, val icon: ImageVector)