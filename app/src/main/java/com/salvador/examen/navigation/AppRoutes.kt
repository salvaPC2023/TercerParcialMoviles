package com.salvador.examen.navigation

sealed class AppRoutes(val route: String) {
    object Plans : AppRoutes("plans")
}