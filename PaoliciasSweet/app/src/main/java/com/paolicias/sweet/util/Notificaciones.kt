package com.paolicias.sweet.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.paolicias.sweet.MainActivity

/** Notificaciones locales para avisar nuevas reservas. */
object Notificaciones {

    const val CANAL_RESERVAS = "reservas"

    /** Crea el canal (obligatorio desde Android 8). Se llama una vez en MainActivity. */
    fun crearCanal(context: Context) {
        val canal = NotificationChannel(
            CANAL_RESERVAS,
            "Nuevas reservas",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Avisos cuando se registra una reserva de evento" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    fun tienePermiso(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    /** Si el usuario nego el permiso, no se notifica y la app sigue funcionando. */
    @SuppressLint("MissingPermission")
    fun nuevaReserva(context: Context, eventoId: Long, fecha: String, total: Int) {
        if (!tienePermiso(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_RESERVAS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Nueva reserva #$eventoId")
            .setContentText("Evento el $fecha · Total ${Formato.clp(total)}. Pendiente de abono.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(eventoId.toInt(), notificacion)
    }
}
