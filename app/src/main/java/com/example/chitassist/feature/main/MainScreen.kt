package com.example.chitassist.feature.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
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
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
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
                    }
                )
            }
            composable(Screen.Calculator.route) {
                CalculatorScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBackClick = { navController.popBackStack() },
                    onLogoutClick = onLogout,
                    onEditProfileClick = { rootNavController.navigate(Screen.EditProfile.route) }
                )
            }
        }
    }
}
