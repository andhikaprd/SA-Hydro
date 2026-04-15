package com.example.smartagro

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Monitor : Screen("monitor")
    object Notification : Screen("notification")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object About : Screen("about")
    object Help : Screen("help")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val viewModel: SmartAgroViewModel = viewModel()

    val bottomNavRoutes = listOf(
        Screen.Dashboard.route,
        Screen.Monitor.route,
        Screen.Notification.route,
        Screen.Profile.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                val currentTitle = when (currentRoute) {
                    Screen.Dashboard.route -> "Dashboard"
                    Screen.Monitor.route -> "Monitor"
                    Screen.Notification.route -> "Notifikasi"
                    Screen.Profile.route -> "Profil"
                    Screen.Settings.route -> "Pengaturan"
                    else -> ""
                }

                SmartAgroBottomNavWithActions(
                    currentRouteTitle = currentTitle,
                    onNavigate = { targetRoute ->
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Dashboard.route) { 
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToMonitor = { navController.navigate(Screen.Monitor.route) },
                    onNavigateToHistory = { navController.navigate(Screen.Monitor.route) }
                ) 
            }
            composable(Screen.Monitor.route) { MonitorScreen(viewModel) }
            composable(Screen.Notification.route) { NotificationScreen() }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToAbout = { navController.navigate(Screen.About.route) },
                    onNavigateToHelp = { navController.navigate(Screen.Help.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Profile.route) { ProfileScreen(onBack = { navController.popBackStack() }) }
            composable(Screen.About.route) { AboutScreen(navController) }
            composable(Screen.Help.route) { HelpGuideScreen(navController) }
        }
    }
}

@Composable
fun SmartAgroBottomNavWithActions(
    currentRouteTitle: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem("Dashboard", Icons.Outlined.Dashboard, Icons.Filled.Dashboard, Screen.Dashboard.route),
            BottomNavItem("Monitor", Icons.Outlined.Thermostat, Icons.Filled.Thermostat, Screen.Monitor.route),
            BottomNavItem("Notifikasi", Icons.Outlined.Notifications, Icons.Filled.Notifications, Screen.Notification.route),
            BottomNavItem("Profil", Icons.Outlined.Person, Icons.Filled.Person, Screen.Profile.route),
            BottomNavItem("Pengaturan", Icons.Outlined.Settings, Icons.Filled.Settings, Screen.Settings.route)
        )

        items.forEach { item ->
            val selected = item.title == currentRouteTitle
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        tint = if (selected) Color(0xFF4CAF50) else Color.Gray
                    )
                },
                label = { Text(text = item.title, fontSize = 10.sp, color = if (selected) Color(0xFF4CAF50) else Color.Gray) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)