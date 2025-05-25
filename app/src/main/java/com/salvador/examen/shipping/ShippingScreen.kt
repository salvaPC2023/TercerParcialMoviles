@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.lainus.examen.shipping

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.lainus.domain.Plan
import com.lainus.domain.ShippingData
import androidx.compose.foundation.text.KeyboardOptions

// Eliminar la data class local ya que existe en domain

@Composable
fun ShippingScreen(
    selectedPlan: Plan,
    onBackClick: () -> Unit,
    onContinue: () -> Unit,
    viewModel: ShippingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val shippingData by viewModel.shippingData.collectAsState()

    // Configurar el plan seleccionado
    LaunchedEffect(selectedPlan) {
        viewModel.setSelectedPlan(selectedPlan)
    }

    // Launcher para permisos de ubicación (solo cuando el usuario lo solicite)
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            getCurrentLocation(context) { lat, lng ->
                viewModel.updateLocation(lat, lng)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // App Bar
        CenterAlignedTopAppBar(
            title = { Text("Envío de SIM") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFF5252),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Reducir espacio entre elementos
        ) {
            // Información del plan seleccionado
            PlanSummaryCard(plan = selectedPlan)

            // Formulario
            ShippingForm(
                shippingData = shippingData,
                onPhoneChange = viewModel::updateReferencePhone,
                onLocationChange = viewModel::updateLocation,
                locationPermissionLauncher = locationPermissionLauncher
            )

            // Botón continuar - ROJO
            Button(
                onClick = {
                    viewModel.submitShippingData()
                    onContinue()
                },
                enabled = viewModel.isDataValid(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp), // Reducir altura
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5252) // Mantener rojo
                )
            ) {
                Text(
                    text = "Continuar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun PlanSummaryCard(plan: Plan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // Cambiar a blanco
    ) {
        Column(
            modifier = Modifier.padding(12.dp) // Reducir padding
        ) {
            Text(
                text = "Plan seleccionado",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = plan.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5252)
            )
            Text(
                text = "${plan.discountedPrice}/mes - ${plan.dataAmount}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ShippingForm(
    shippingData: ShippingData,
    onPhoneChange: (String) -> Unit,
    onLocationChange: (Double, Double) -> Unit,
    locationPermissionLauncher: androidx.activity.compose.ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // Cambiar a blanco
    ) {
        Column(
            modifier = Modifier.padding(12.dp), // Reducir padding
            verticalArrangement = Arrangement.spacedBy(12.dp) // Reducir espacio
        ) {
            Text(
                text = "Información de envío",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // Campo teléfono con menos altura
            OutlinedTextField(
                value = shippingData.referencePhone,
                onValueChange = onPhoneChange,
                label = { Text("Teléfono de referencia") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Sección de ubicación
            LocationSection(
                latitude = shippingData.latitude,
                longitude = shippingData.longitude,
                onLocationChange = onLocationChange,
                locationPermissionLauncher = locationPermissionLauncher
            )
        }
    }
}

@Composable
private fun LocationSection(
    latitude: Double,
    longitude: Double,
    onLocationChange: (Double, Double) -> Unit,
    locationPermissionLauncher: androidx.activity.compose.ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
) {
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp) // Reducir espacio
    ) {
        Text(
            text = "Ubicación de entrega",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        // Campos de coordenadas más compactos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp) // Reducir espacio
        ) {
            OutlinedTextField(
                value = if (latitude != 0.0) "%.4f".format(latitude) else "", // Formato más corto
                onValueChange = { },
                label = { Text("Latitud", fontSize = 12.sp) }, // Texto más pequeño
                readOnly = true,
                modifier = Modifier.weight(1f),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp) // Texto más pequeño
            )
            OutlinedTextField(
                value = if (longitude != 0.0) "%.4f".format(longitude) else "", // Formato más corto
                onValueChange = { },
                label = { Text("Longitud", fontSize = 12.sp) }, // Texto más pequeño
                readOnly = true,
                modifier = Modifier.weight(1f),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp) // Texto más pequeño
            )
        }

        // Botón obtener mi ubicación - NEGRO
        Button(
            onClick = {
                val hasLocationPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (hasLocationPermission) {
                    getCurrentLocation(context) { lat, lng ->
                        onLocationChange(lat, lng)
                    }
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black // Cambiar a negro
            )
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Obtener mi ubicación", color = Color.White)
        }

        // Mapa
        MapSection(
            latitude = latitude,
            longitude = longitude,
            onLocationSelected = onLocationChange
        )
    }
}

@Composable
private fun MapSection(
    latitude: Double,
    longitude: Double,
    onLocationSelected: (Double, Double) -> Unit
) {
    var mapProperties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = false,
                mapStyleOptions = null
            )
        )
    }

    // Centrar en Bolivia por defecto (La Paz)
    val defaultLocation = LatLng(-16.5000, -68.1500) // Centro de Bolivia
    val currentLocation = if (latitude != 0.0 && longitude != 0.0) {
        LatLng(latitude, longitude)
    } else {
        defaultLocation
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 6f) // Zoom 6 para ver todo Bolivia
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            onMapClick = { latLng ->
                onLocationSelected(latLng.latitude, latLng.longitude)
            }
        ) {
            if (latitude != 0.0 && longitude != 0.0) {
                Marker(
                    state = MarkerState(position = LatLng(latitude, longitude)),
                    title = "Ubicación seleccionada"
                )
            }
        }
    }

    Text(
        text = "Toca en el mapa para seleccionar la ubicación de entrega",
        fontSize = 12.sp,
        color = Color.Gray,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    context: android.content.Context,
    onLocationReceived: (Double, Double) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            onLocationReceived(it.latitude, it.longitude)
        }
    }
}