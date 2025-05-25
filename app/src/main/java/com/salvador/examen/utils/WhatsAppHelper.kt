package com.salvador.examen.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.salvador.domain.Plan

object WhatsAppHelper {

    fun openWhatsApp(context: Context, plan: Plan) {
        val phoneNumber = "+591XXXXXXXX" // Reemplazar con el número de WhatsApp Business
        val message = buildMessage(plan)

        val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Si WhatsApp no está instalado, abrir en el navegador
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(browserIntent)
        }
    }

    private fun buildMessage(plan: Plan): String {
        return """
            Hola! Me interesa el ${plan.name}.
            
            Detalles del plan:
            • Datos: ${plan.dataAmount}
            • Precio: $${plan.discountedPrice}/mes
            • Precio anterior: $${plan.originalPrice}/mes
            
            ¿Podrían darme más información?
        """.trimIndent()
    }
}