package com.example.chitassist.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem(Screen.Dashboard.route, "Home", Icons.Default.Dashboard)
    object Chits : BottomNavItem(Screen.ChitList.route, "Chits", Icons.AutoMirrored.Filled.List)
    object Calculator : BottomNavItem(Screen.Calculator.route, "Calc", Icons.Default.Calculate)
    object Profile : BottomNavItem(Screen.Profile.route, "Profile", Icons.Default.Person)
}
