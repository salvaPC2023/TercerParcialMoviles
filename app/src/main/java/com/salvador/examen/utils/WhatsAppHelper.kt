package com.salvador.examen.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

object WhatsAppHelper {

    fun openWhatsApp(context: Context) {
        val phone = "+59162061891"
        val message = "Hola, pido tu ayuda"

        // Método 1: Intentar con wa.me directamente
        try {
            val uri = Uri.parse("https://wa.me/$phone/?text=${Uri.encode(message)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)

            // Verificar si alguna app puede manejar este intent
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                return
            }
        } catch (e: Exception) {
            // Continuar con el siguiente método
        }

        // Método 2: Intent directo a WhatsApp
        try {
            val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                putExtra("jid", "$phone@s.whatsapp.net")
            }

            // Intentar con WhatsApp normal
            whatsappIntent.setPackage("com.whatsapp")
            if (whatsappIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(whatsappIntent)
                return
            }

            // Intentar con WhatsApp Business
            whatsappIntent.setPackage("com.whatsapp.w4b")
            if (whatsappIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(whatsappIntent)
                return
            }

        } catch (e: Exception) {
            // Continuar con el siguiente método
        }

        // Método 3: URI de WhatsApp directo
        try {
            val whatsappUri = Uri.parse("whatsapp://send?phone=$phone&text=${Uri.encode(message)}")
            val whatsappIntent = Intent(Intent.ACTION_VIEW, whatsappUri)

            if (whatsappIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(whatsappIntent)
                return
            }
        } catch (e: Exception) {
            // Continuar al error final
        }

        // Si llegamos aquí, ningún método funcionó
        Toast.makeText(context, "WhatsApp no está disponible", Toast.LENGTH_LONG).show()
    }

    // Función para debug - verificar qué está disponible
    fun debugAvailableApps(context: Context) {
        val testIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/123456789"))
        val activities = context.packageManager.queryIntentActivities(testIntent, 0)

        for (activity in activities) {
            println("Available app: ${activity.activityInfo.packageName}")
        }

        // También verificar paquetes específicos
        val packages = listOf("com.whatsapp", "com.whatsapp.w4b")
        for (pkg in packages) {
            try {
                context.packageManager.getPackageInfo(pkg, 0)
                println("Package $pkg is installed")
            } catch (e: PackageManager.NameNotFoundException) {
                println("Package $pkg is NOT installed")
            }
        }
    }
}