package com.salvador.examen.plans

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.salvador.domain.Plan
import com.salvador.examen.R
import com.salvador.examen.utils.WhatsAppHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlansUI(
    viewModel: PlansViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsState()
    val currentIndex by viewModel.currentPlanIndex.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        when (val currentState = state) {
            is PlansViewModel.PlansState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFFFF6B6B)
                )
            }
            is PlansViewModel.PlansState.Success -> {
                PlansContent(
                    plans = currentState.plans,
                    currentIndex = currentIndex,
                    onPlanIndexChanged = viewModel::onPlanIndexChanged,
                    onPlanSelected = viewModel::onPlanSelected,
                    viewModel = viewModel,
                    context = context,
                    navController = navController
                )
            }
            is PlansViewModel.PlansState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Oops! Algo salió mal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = "${currentState.message}",
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PlansContent(
    plans: List<Plan>,
    currentIndex: Int,
    onPlanIndexChanged: (Int) -> Unit,
    onPlanSelected: (Plan) -> Unit,
    viewModel: PlansViewModel,
    context: android.content.Context,
    navController: NavHostController
) {
    val pagerState = rememberPagerState(pageCount = { plans.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        onPlanIndexChanged(pagerState.currentPage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header más elegante
        Text(
            text = "Nuestros planes móviles",
            color = Color(0xFFFF6B6B),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Text(
            text = "La mejor cobertura, increíbles beneficios y un compromiso con el planeta.",
            color = Color(0xFF666666),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 12.dp)
        )

        // Contenedor principal con navegación
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Pager con los cards
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 40.dp),
                pageSpacing = 20.dp
            ) { page ->
                PlanCard(
                    plan = plans[page],
                    isSelected = page == currentIndex,
                    onSelectPlan = { onPlanSelected(plans[page]) },
                    context = context,
                    navController = navController
                )
            }

            // Botones de navegación mejorados
            if (currentIndex > 0) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(currentIndex - 1)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 4.dp)
                        .size(44.dp)
                        .background(
                            Color.White.copy(alpha = 0.9f), 
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Anterior",
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            if (currentIndex < plans.size - 1) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(currentIndex + 1)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp)
                        .size(44.dp)
                        .background(
                            Color.White.copy(alpha = 0.9f), 
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Siguiente",
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Indicadores de página mejorados
        Row(
            modifier = Modifier.padding(bottom = 32.dp, top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            plans.indices.forEach { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(
                            width = if (index == currentIndex) 28.dp else 8.dp,
                            height = 8.dp
                        )
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (index == currentIndex) 
                                Color(0xFFFF6B6B) 
                            else 
                                Color(0xFFE0E0E0)
                        )
                )
            }
        }
    }
}

@Composable
private fun PlanCard(
    plan: Plan,
    isSelected: Boolean,
    onSelectPlan: () -> Unit,
    context: android.content.Context,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(520.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 12.dp else 6.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Plan name con mejor estilo
            Text(
                text = plan.name,
                color = Color(0xFFFF6B6B),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Línea decorativa
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(3.dp)
                    .background(
                        Color(0xFFFF6B6B),
                        RoundedCornerShape(2.dp)
                    )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Sección de precios mejorada
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Precio original
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Antes ",
                        color = Color(0xFF999999),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${plan.originalPrice}",
                        color = Color(0xFF999999),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textDecoration = TextDecoration.LineThrough
                    )
                    Text(
                        text = " /mes",
                        color = Color(0xFF999999),
                        fontSize = 12.sp
                    )
                }

                // Precio actual
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Ahora ",
                        color = Color(0xFF666666),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "${plan.discountedPrice}",
                        color = Color(0xFF333333),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " /mes",
                        color = Color(0xFF666666),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                    )
                }

                // Data amount destacado
                Text(
                    text = plan.dataAmount,
                    color = Color(0xFF333333),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Features con mejor diseño
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                plan.features.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 1.dp)
                        )
                        Text(
                            text = feature,
                            color = Color(0xFF555555),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Redes sociales con mejor espaciado
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                plan.socialNetworks.forEach { network ->
                    SocialNetworkIcon(network)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón mejorado con navegación
            Button(
                onClick = {
                    onSelectPlan()
                    navController.navigate("shipping_screen")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B6B)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Text(
                    text = "Quiero este plan",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_whatsapp),
                    contentDescription = "WhatsApp"
                )
            }
        }
    }
}

@Composable
private fun SocialNetworkIcon(network: String) {
    val iconRes = when (network) {
        "whatsapp" -> R.drawable.ic_whatsapp
        "facebook" -> R.drawable.ic_facebook
        "twitter" -> R.drawable.ic_twitter
        "instagram" -> R.drawable.ic_instagram
        "snapchat" -> R.drawable.ic_snapchat
        "telegram" -> R.drawable.ic_telegram
        else -> null
    }

    iconRes?.let {
        Icon(
            painter = painterResource(id = it),
            contentDescription = network,
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .size(26.dp),
            tint = Color.Unspecified
        )
    }
}