package com.salvador.examen.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.salvador.examen.plans.PlansUI
import com.salvador.examen.plans.PlansViewModel
import com.salvador.examen.shipping.ShippingScreen

@Composable
fun PlansNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val sharedPlansViewModel: PlansViewModel = hiltViewModel()

    println("Iniciando con AppRoutes")
    NavHost(
        navController = navController,
        startDestination = AppRoutes.Plans.route,
        modifier = modifier
    ) {
        composable(AppRoutes.Plans.route) {
            println("Composable 'plans' esta ejecutándose")
            PlansUI(
                navController = navController,
                viewModel = sharedPlansViewModel
            )
        }

        composable(AppRoutes.Shipping.route) {
            println("Entrando a composable de shipping")

            val selectedPlan by sharedPlansViewModel.selectedPlan.collectAsState()

            if (selectedPlan != null) {
                ShippingScreen(
                    selectedPlan = selectedPlan!!,
                    onBackClick = {
                        println("Back presionado en ShippingScreen")
                        navController.popBackStack()
                    },
                    onContinue = {
                        println("Continua presionado en ShippingScreen")
                        navController.popBackStack()
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Cargando el plan que selecciono")
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Volver")
                        }
                    }
                }
            }
        }
    }
}