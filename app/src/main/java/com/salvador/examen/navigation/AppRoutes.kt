package com.salvador.examen.navigation

sealed class AppRoutes(val route: String) {
    object BookSearch : AppRoutes("book_search")
    object Favorites : AppRoutes("favorites")
}