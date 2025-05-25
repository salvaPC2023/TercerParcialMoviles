package com.salvador.examen.navigation

sealed class AppRoutes(val route: String) {
    object Plans : AppRoutes("plans")
    object Shipping : AppRoutes("shipping_screen")

    init {
        println("DEBUG: AppRoutes creado - route: $route")
    }
}