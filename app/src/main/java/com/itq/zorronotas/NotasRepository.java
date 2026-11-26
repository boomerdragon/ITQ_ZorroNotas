package com.itq.zorronotas;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositorio desacoplado que gestiona la ejecución de operaciones CRUD en segundo plano.
 */
public class NotasRepository {

    private final Application application;
    private final NotaDao notaDao;
    private final LiveData<List<NotaEntity>> todasLasNotas;
    private final ExecutorService executorService;

    public NotasRepository(Application application) {
        this.application = application;
        AppDatabase db = AppDatabase.getInstancia(application);
        notaDao = db.notaDao();
        todasLasNotas = notaDao.getTodasLasNotas();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<NotaEntity>> getTodasLasNotas() {
        return todasLasNotas;
    }

    public void insertar(NotaEntity nota) {
        executorService.execute(() -> {
            long id = notaDao.insertar(nota);
            nota.setId((int) id);
            if (nota.getFechaRecordatorio() != null && nota.getFechaRecordatorio() > System.currentTimeMillis()) {
                ReminderScheduler.programarRecordatorio(
                        application.getApplicationContext(),
                        nota.getId(),
                        nota.getTitulo(),
                        nota.getContenido(),
                        nota.getFechaRecordatorio()
                );
            }
        });
    }

    public void actualizar(NotaEntity nota) {
        executorService.execute(() -> {
            notaDao.actualizar(nota);
            if (nota.getFechaRecordatorio() != null && nota.getFechaRecordatorio() > System.currentTimeMillis()) {
                ReminderScheduler.programarRecordatorio(
                        application.getApplicationContext(),
                        nota.getId(),
                        nota.getTitulo(),
                        nota.getContenido(),
                        nota.getFechaRecordatorio()
                );
            } else {
                ReminderScheduler.cancelarRecordatorio(application.getApplicationContext(), nota.getId());
            }
        });
    }

    public void eliminar(NotaEntity nota) {
        executorService.execute(() -> {
            notaDao.eliminar(nota);
            ReminderScheduler.cancelarRecordatorio(application.getApplicationContext(), nota.getId());
        });
    }
}