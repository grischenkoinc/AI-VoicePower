package com.aivoicepower.ui.screens.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DrawerNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val selectedColor: Color = Color(0xFF8B5CF6)
)

@Composable
fun AppDrawerContent(
    currentRoute: String?,
    userName: String?,
    userEmail: String?,
    isAuthenticated: Boolean,
    isPremium: Boolean,
    onNavigate: (String) -> Unit,
    onNavigateToAiCoach: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        DrawerNavItem("home", "Головна", Icons.Filled.Home, Icons.Outlined.Home, Color(0xFF667EEA)),
        DrawerNavItem("courses", "Курси", Icons.Filled.School, Icons.Outlined.School, Color(0xFF8B5CF6)),
        DrawerNavItem("warmup", "Розминка", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, Color(0xFFEC4899)),
        DrawerNavItem("improvisation", "Імпровізація", Icons.Filled.Lightbulb, Icons.Outlined.Lightbulb, Color(0xFFF59E0B)),
        DrawerNavItem("progress", "Прогрес", Icons.AutoMirrored.Filled.TrendingUp, Icons.AutoMirrored.Outlined.TrendingUp, Color(0xFF10B981))
    )

    ModalDrawerSheet(
        drawerContainerColor = Color(0xFF1A1625),
        modifier = modifier.width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            DrawerHeader(
                userName = userName,
                userEmail = userEmail,
                isAuthenticated = isAuthenticated,
                isPremium = isPremium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Navigation items
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            tint = if (isSelected) item.selectedColor else Color.White.copy(alpha = 0.6f)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                        )
                    },
                    selected = isSelected,
                    onClick = { onNavigate(item.route) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color.White.copy(alpha = 0.1f),
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
            }

            // AI Coach
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Тренер",
                        tint = Color(0xFFA78BFA)
                    )
                },
                label = {
                    Text(
                        text = "AI Тренер",
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                },
                selected = false,
                onClick = onNavigateToAiCoach,
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            // Premium
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Преміум",
                        tint = Color(0xFFFBBF24)
                    )
                },
                label = {
                    Text(
                        text = "Преміум",
                        fontSize = 15.sp,
                        color = Color(0xFFFBBF24)
                    )
                },
                selected = false,
                onClick = onNavigateToPremium,
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )

            // Settings
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Налаштування",
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        text = "Налаштування",
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                },
                selected = false,
                onClick = onNavigateToSettings,
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Version
            Text(
                text = "AI VoicePower v1.0.0",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 24.dp)
            )
        }
    }
}
