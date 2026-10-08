package com.example.chitassist.core.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object Dashboard : Screen("dashboard")
    object ChitList : Screen("chit_list")
    object ChitAdd : Screen("chit_add?chitId={chitId}") {
        fun createRoute(chitId: Long? = null) = if (chitId != null) "chit_add?chitId=$chitId" else "chit_add"
    }
    object ChitDetails : Screen("chit_details/{chitId}") {
        fun createRoute(chitId: Long) = "chit_details/$chitId"
    }
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Calculator : Screen("calculator")
}
