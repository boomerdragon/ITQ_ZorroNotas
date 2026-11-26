package com.itq.zorronotas;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Clase utilitaria para programar y cancelar alarmas de recordatorio utilizando AlarmManager.
 */
public class ReminderScheduler {

    public static void programarRecordatorio(Context context, int notaId, String titulo, String contenido, long timeInMillis) {
        if (timeInMillis <= System.currentTimeMillis()) {
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(ReminderReceiver.EXTRA_NOTA_ID, notaId);
        intent.putExtra(ReminderReceiver.EXTRA_NOTA_TITULO, titulo);
        intent.putExtra(ReminderReceiver.EXTRA_NOTA_CONTENIDO, contenido);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notaId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } catch (SecurityException e) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }
    }

    public static void cancelarRecordatorio(Context context, int notaId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notaId,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
