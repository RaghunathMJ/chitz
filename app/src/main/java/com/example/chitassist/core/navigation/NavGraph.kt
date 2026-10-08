package com.example.chitassist.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.chitassist.feature.auth.login.LoginScreen
import com.example.chitassist.feature.auth.register.RegisterScreen
import com.example.chitassist.feature.chits.add.AddChitScreen
import com.example.chitassist.feature.chits.details.ChitDetailsScreen
import com.example.chitassist.feature.main.MainScreen
import com.example.chitassist.feature.profile.edit.EditProfileScreen
import com.example.chitassist.feature.profile.profile.ProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                rootNavController = navController,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ChitList.route) {
            // Handled in MainScreen
        }

        composable(
            route = Screen.ChitDetails.route,
            arguments = listOf(navArgument("chitId") { type = NavType.LongType })
        ) {
            ChitDetailsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ChitAdd.route,
            arguments = listOf(navArgument("chitId") { 
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val chitId = backStackEntry.arguments?.getLong("chitId") ?: -1L
            AddChitScreen(
                chitId = if (chitId != -1L) chitId else null,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            // Handled in MainScreen
        }

        composable(Screen.Calculator.route) {
            // Handled in MainScreen
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
