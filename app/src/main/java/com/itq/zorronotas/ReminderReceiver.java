package com.itq.zorronotas;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationCompat;

/**
 * BroadcastReceiver que se activa cuando expira una alarma de recordatorio
 * y muestra una notificación para reabrir la aplicación y la nota correspondiente.
 */
public class ReminderReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "canal_recordatorios_notas";
    public static final String EXTRA_NOTA_ID = "extra_nota_id";
    public static final String EXTRA_NOTA_TITULO = "extra_nota_titulo";
    public static final String EXTRA_NOTA_CONTENIDO = "extra_nota_contenido";

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(EXTRA_NOTA_ID, -1);
        String titulo = intent.getStringExtra(EXTRA_NOTA_TITULO);
        String contenido = intent.getStringExtra(EXTRA_NOTA_CONTENIDO);

        String tituloNotificacion = (titulo != null && !titulo.trim().isEmpty()) ? titulo : "Recordatorio de nota";
        String contenidoNotificacion = (contenido != null) ? contenido : "Tienes una nota pendiente";

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Recordatorios de Notas",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones de recordatorios para notas");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Intent para reabrir la app y la nota correspondiente
        Intent notifyIntent = new Intent(context, MainActivity.class);
        notifyIntent.putExtra(AgregarEditarNotaActivity.EXTRA_ID, id);
        notifyIntent.putExtra(AgregarEditarNotaActivity.EXTRA_TITULO, tituloNotificacion);
        notifyIntent.putExtra(AgregarEditarNotaActivity.EXTRA_CONTENIDO, contenidoNotificacion);
        notifyIntent.putExtra("ABRIR_NOTA_RECORDATORIO", true);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                id > 0 ? id : (int) System.currentTimeMillis(),
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("🔔 Recordatorio: " + tituloNotificacion)
                .setContentText(contenidoNotificacion)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(id > 0 ? id : (int) System.currentTimeMillis(), builder.build());
            }
        }
    }
}
