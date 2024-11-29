package com.igxd.blocknotas2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.igxd.blocknotas2.R

class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "recordatorio_channel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Crear el canal de notificación (requiere para versiones Android 8 y superiores)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recordatorio",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal de notificaciones para recordatorios"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Crear la notificación
        val notification: Notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Asegúrate de tener un ícono de notificación
            .setContentTitle("¡Recordatorio!")
            .setContentText("Es hora de revisar tu nota.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Enviar la notificación
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }
}
