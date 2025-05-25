@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.salvador.examen.shipping

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
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
import com.salvador.domain.Plan
import com.salvador.domain.ShippingData
import androidx.compose.foundation.text.KeyboardOptions

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

    // Launcher para permisos de ubicación
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // App Bar mejorada
        CenterAlignedTopAppBar(
            title = { 
                Text(
                    "Configuración de Envío",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ) 
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack, 
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información del plan seleccionado
            PlanSummaryCard(plan = selectedPlan)

            // Formulario de envío
            ShippingForm(
                shippingData = shippingData,
                onPhoneChange = viewModel::updateReferencePhone,
                onLocationChange = viewModel::updateLocation,
                locationPermissionLauncher = locationPermissionLauncher
            )

            // Botón continuar
            Button(
                onClick = {
                    viewModel.submitShippingData()
                    onContinue()
                },
                enabled = viewModel.isDataValid(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5252),
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp
                )
            ) {
                Text(
                    text = "Confirmar Envío",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PlanSummaryCard(plan: Plan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Plan Seleccionado",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = plan.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5252)
            )
            
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${plan.discountedPrice}/mes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = " • ",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(
                    text = plan.dataAmount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Información de Contacto",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Campo teléfono mejorado
            OutlinedTextField(
                value = shippingData.referencePhone,
                onValueChange = onPhoneChange,
                label = { 
                    Text(
                        "Teléfono de referencia",
                        fontSize = 14.sp
                    ) 
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = Color(0xFFFF5252)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF5252),
                    focusedLabelColor = Color(0xFFFF5252)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Divider(
                color = Color.Gray.copy(alpha = 0.3f),
                thickness = 1.dp
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Ubicación de Entrega",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // Mostrar coordenadas actuales
        if (latitude != 0.0 && longitude != 0.0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0F8FF)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Ubicación seleccionada:",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Lat: ${String.format("%.4f", latitude)}, Lng: ${String.format("%.4f", longitude)}",
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Botón obtener ubicación
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
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3)
            )
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Obtener Mi Ubicación Actual",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Mapa mejorado
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
    val context = LocalContext.current
    
    var mapProperties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = false,
                mapStyleOptions = null
            )
        )
    }

    // Centrar en Bolivia por defecto (La Paz)
    val defaultLocation = LatLng(-16.5000, -68.1500)
    val currentLocation = if (latitude != 0.0 && longitude != 0.0) {
        LatLng(latitude, longitude)
    } else {
        defaultLocation
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, if (latitude != 0.0) 15f else 6f)
    }

    // Actualizar cámara cuando cambie la ubicación
    LaunchedEffect(latitude, longitude) {
        if (latitude != 0.0 && longitude != 0.0) {
            cameraPositionState.animate(
                CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 15f)
            )
        }
    }

    Column {
        Text(
            text = "Selecciona la ubicación en el mapa:",
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    compassEnabled = true,
                    myLocationButtonEnabled = false
                ),
                onMapClick = { latLng ->
                    onLocationSelected(latLng.latitude, latLng.longitude)
                }
            ) {
                if (latitude != 0.0 && longitude != 0.0) {
                    Marker(
                        state = MarkerState(position = LatLng(latitude, longitude)),
                        title = "Ubicación de entrega",
                        snippet = "Aquí se enviará tu SIM"
                    )
                }
            }
        }

        Text(
            text = "💡 Toca en cualquier lugar del mapa para seleccionar la ubicación de entrega",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp),
            lineHeight = 16.sp
        )
    }
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
    }.addOnFailureListener {
        // Si falla, usar ubicación por defecto de La Paz
        onLocationReceived(-16.5000, -68.1500)
    }
}