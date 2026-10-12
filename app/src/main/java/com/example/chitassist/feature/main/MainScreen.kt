package com.example.chitassist.feature.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chitassist.core.navigation.BottomNavItem
import com.example.chitassist.core.navigation.Screen
import com.example.chitassist.feature.calculator.CalculatorScreen
import com.example.chitassist.feature.chits.list.ChitListScreen
import com.example.chitassist.feature.dashboard.DashboardScreen
import com.example.chitassist.feature.profile.profile.ProfileScreen

@Composable
fun MainScreen(
    rootNavController: NavHostController,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Chits,
        BottomNavItem.Calculator,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                item.icon, 
                                contentDescription = item.title,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            ) 
                        },
                        label = { 
                            Text(
                                item.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            ) 
                        },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            unselectedIconColor = MaterialTheme.colorScheme.outline,
                            unselectedTextColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToAddChit = { rootNavController.navigate(Screen.ChitAdd.createRoute()) },
                    onNavigateToEditChit = { chitId ->
                        rootNavController.navigate(Screen.ChitAdd.createRoute(chitId))
                    },
                    onNavigateToChitDetails = { chitId -> 
                        rootNavController.navigate(Screen.ChitDetails.createRoute(chitId)) 
                    },
                    onNavigateToProfile = { 
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToChitList = { 
                        navController.navigate(Screen.ChitList.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLogout = onLogout
                )
            }
            composable(Screen.ChitList.route) {
                ChitListScreen(
                    onChitClick = { chitId ->
                        rootNavController.navigate(Screen.ChitDetails.createRoute(chitId))
                    },
                    onEditChit = { chitId ->
                        rootNavController.navigate(Screen.ChitAdd.createRoute(chitId))
                    },
                    onAddChit = { rootNavController.navigate(Screen.ChitAdd.createRoute()) }
                )
            }
            composable(Screen.Calculator.route) {
                CalculatorScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBackClick = { 
                        if (!navController.popBackStack()) {
                            // Already at start, maybe do nothing or handle accordingly
                        }
                    },
                    onLogoutClick = onLogout,
                    onEditProfileClick = { rootNavController.navigate(Screen.EditProfile.route) }
                )
            }
        }
    }
}
