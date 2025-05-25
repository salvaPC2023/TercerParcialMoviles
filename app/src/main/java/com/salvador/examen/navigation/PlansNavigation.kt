package com.salvador.examen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.salvador.examen.plans.PlansUI

@Composable
fun PlansNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.Plans.route,
        modifier = modifier
    ) {
        composable(AppRoutes.Plans.route) {
            PlansUI()
        }
    }
}