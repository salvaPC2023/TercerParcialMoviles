package com.salvador.framework.plan

import com.salvador.data.NetworkResult
import com.salvador.data.plan.IPlanDataSource
import com.salvador.domain.Plan
import kotlinx.coroutines.delay

class PlanDataSource : IPlanDataSource {

    override suspend fun fetchPlans(): NetworkResult<List<Plan>> {
        // Simulamos una llamada a la API con datos hardcodeados
        delay(500) // Simular latencia de red

        val plans = listOf(
            Plan(
                id = "flex_5",
                name = "Plan FLEX 5",
                dataAmount = "5GB",
                originalPrice = 270,
                discountedPrice = 199,
                features = listOf(
                    "Llamadas y SMS ilimitados",
                    "Hotspot. Comparte tus datos",
                    "Redes Sociales ilimitadas incluidas",
                    "Arma tu plan con más apps ilimitadas",
                    "CO2 Negativo"
                ),
                socialNetworks = listOf(
                    "whatsapp",
                    "facebook",
                    "twitter",
                    "instagram",
                    "snapchat",
                    "telegram"
                )
            ),
            Plan(
                id = "flex_8",
                name = "Plan FLEX 8",
                dataAmount = "8GB",
                originalPrice = 370,
                discountedPrice = 299,
                features = listOf(
                    "Llamadas y SMS ilimitados",
                    "Hotspot. Comparte tus datos",
                    "Redes Sociales ilimitadas incluidas",
                    "Arma tu plan con más apps ilimitadas",
                    "CO2 Negativo"
                ),
                socialNetworks = listOf(
                    "whatsapp",
                    "facebook",
                    "twitter",
                    "instagram",
                    "snapchat",
                    "telegram"
                )
            ),
            Plan(
                id = "flex_10",
                name = "Plan FLEX 10",
                dataAmount = "10GB",
                originalPrice = 470,
                discountedPrice = 399,
                features = listOf(
                    "Llamadas y SMS ilimitados",
                    "Hotspot. Comparte tus datos",
                    "Redes Sociales ilimitadas incluidas",
                    "Arma tu plan con más apps ilimitadas",
                    "CO2 Negativo"
                ),
                socialNetworks = listOf(
                    "whatsapp",
                    "facebook",
                    "twitter",
                    "instagram",
                    "snapchat",
                    "telegram"
                )
            )
        )

        return NetworkResult.Success(plans)
    }
}