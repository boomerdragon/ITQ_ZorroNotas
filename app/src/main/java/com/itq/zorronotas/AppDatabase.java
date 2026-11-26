package com.itq.zorronotas;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Base de datos Room en patrón Singleton para evitar la apertura repetida de conexiones SQLite.
 */
@Database(entities = {NotaEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCIA;

    public abstract NotaDao notaDao();

    public static AppDatabase getInstancia(final Context context) {
        if (INSTANCIA == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCIA == null) {
                    INSTANCIA = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "notas_rapidas_db"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCIA;
    }
}